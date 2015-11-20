module api.content.site.inputtype.siteconfigurator {

    import AEl = api.dom.AEl;
    import PropertyTree = api.data.PropertyTree;
    import PropertySet = api.data.PropertySet;
    import Option = api.ui.selector.Option;
    import FormView = api.form.FormView;
    import FormContextBuilder = api.form.FormContextBuilder;
    import Application = api.application.Application;
    import ApplicationKey = api.application.ApplicationKey;
    import SiteConfig = api.content.site.SiteConfig;
    import LoadedDataEvent = api.util.loader.event.LoadedDataEvent;
    import ContentFormContext = api.content.form.ContentFormContext;
    import ContentRequiresSaveEvent = api.content.ContentRequiresSaveEvent;

    export class SiteConfiguratorSelectedOptionView extends api.ui.selector.combobox.BaseSelectedOptionView<Application> {

        private application: Application;

        private formView: FormView;

        private siteConfig: SiteConfig;

        private editClickedListeners: {(event: MouseEvent): void;}[];

        private siteConfigFormDisplayedListeners: {(applicationKey: ApplicationKey) : void}[];

        private formContext: ContentFormContext;

        private formValidityChangedHandler: {(event: api.form.FormValidityChangedEvent):void};

        constructor(option: Option<Application>, siteConfig: SiteConfig, formContext: api.content.form.ContentFormContext) {
            this.editClickedListeners = [];
            this.siteConfigFormDisplayedListeners = [];

            this.application = option.displayValue;
            this.siteConfig = siteConfig;
            this.formContext = formContext;

            super(option);
        }

        layout() {
            var header = new api.dom.DivEl('header');

            var namesAndIconView = new api.app.NamesAndIconView(new api.app.NamesAndIconViewBuilder().
                setSize(api.app.NamesAndIconViewSize.large)).
                setMainName(this.application.getDisplayName()).
                setSubName(this.application.getName() + "-" + this.application.getVersion()).
                setIconClass("icon-xlarge icon-puzzle");

            header.appendChild(namesAndIconView);

            var removeButton = new api.dom.AEl("remove-button icon-close");
            removeButton.onClicked((event: MouseEvent) => this.notifyRemoveClicked());
            header.appendChild(removeButton);

            this.appendChild(header);

            this.initFormView();

            if (this.application.getForm().getFormItems().length > 0) {
                header.appendChild(this.createEditButton());
            }
        }

        private initFormView() {
            this.formValidityChangedHandler = (event: api.form.FormValidityChangedEvent) => {
                this.toggleClass("invalid", !event.isValid())
            }

            this.formView = this.createFormView(this.formContext, this.siteConfig);
        }

        private createEditButton(): api.dom.AEl {
            var editButton = new api.dom.AEl('edit-button');

            editButton.onClicked((event: MouseEvent) => {
                this.notifyEditClicked(event);
                this.initAndOpenConfigureDialog();
            });

            return editButton;
        }

        initAndOpenConfigureDialog(comboBoxToUndoSelectionOnCancel?: SiteConfiguratorComboBox) {

            if (this.application.getForm().getFormItems().length > 0) {

                var tempSiteConfig: SiteConfig = this.makeTemporarySiteConfig();

                var formViewStateOnDialogOpen = this.formView;
                this.unbindValidationEvent(formViewStateOnDialogOpen);

                this.formView = this.createFormView(this.formContext, tempSiteConfig);
                this.bindValidationEvent(this.formView);

                var okCallback = () => {
                    if (!tempSiteConfig.equals(this.siteConfig)) {
                        this.applyTemporaryConfig(tempSiteConfig);
                        new ContentRequiresSaveEvent(this.formContext.getPersistedContent()).fire();
                    }
                };

                var cancelCallback = () => {
                    this.revertFormViewToGivenState(formViewStateOnDialogOpen);
                    if (comboBoxToUndoSelectionOnCancel) {
                        this.undoSelectionOnCancel(comboBoxToUndoSelectionOnCancel);
                    }
                };

                var siteConfiguratorDialog = new SiteConfiguratorDialog(this.application.getDisplayName(),
                    this.application.getName() + "-" + this.application.getVersion(),
                    this.formView,
                    okCallback,
                    cancelCallback);
                siteConfiguratorDialog.open();
            }
        }

        private revertFormViewToGivenState(formViewStateToRevertTo: FormView) {
            this.unbindValidationEvent(this.formView);
            this.formView = formViewStateToRevertTo;
            this.formView.validate(false, true);
            this.toggleClass("invalid", !this.formView.isValid())
        }

        private undoSelectionOnCancel(comboBoxToUndoSelectionOnCancel: SiteConfiguratorComboBox) {
            comboBoxToUndoSelectionOnCancel.deselect(this.application);
        }

        private applyTemporaryConfig(tempSiteConfig: SiteConfig) {
            tempSiteConfig.getConfig().forEach((property) => {
                this.siteConfig.getConfig().setProperty(property.getName(), property.getIndex(), property.getValue());
            });
            this.siteConfig.getConfig().forEach((property) => {
                var prop = tempSiteConfig.getConfig().getProperty(property.getName(), property.getIndex());
                if (!prop) {
                    this.siteConfig.getConfig().removeProperty(property.getName(), property.getIndex());
                }
            });
        }

        private makeTemporarySiteConfig(): SiteConfig {
            var propSet = (new PropertyTree(this.siteConfig.getConfig())).getRoot();
            propSet.setContainerProperty(this.siteConfig.getConfig().getProperty());
            return SiteConfig.create().setConfig(propSet).setApplicationKey(this.siteConfig.getApplicationKey()).build();
        }

        private createFormView(formContext: api.content.form.ContentFormContext, siteConfig: SiteConfig): FormView {
            var formView = new FormView(formContext, this.application.getForm(), siteConfig.getConfig());
            formView.addClass("site-form");
            formView.layout().then(() => {
                this.formView.validate(false, true);
                this.toggleClass("invalid", !this.formView.isValid());
                this.notifySiteConfigFormDisplayed(this.application.getApplicationKey());
            }).catch((reason: any) => {
                api.DefaultErrorHandler.handle(reason);
            }).done();

            return formView;
        }

        private bindValidationEvent(formView: FormView) {
            if (formView) {
                formView.onValidityChanged(this.formValidityChangedHandler);
            }
        }

        private unbindValidationEvent(formView: FormView) {
            if (formView) {
                formView.unValidityChanged(this.formValidityChangedHandler);
            }
        }

        getApplication(): Application {
            return this.application;
        }

        getSiteConfig(): SiteConfig {
            return this.siteConfig;
        }

        getFormView(): FormView {
            return this.formView;
        }

        onEditClicked(listener: (event: MouseEvent) => void) {
            this.editClickedListeners.push(listener);
        }

        unEditClicked(listener: (event: MouseEvent) => void) {
            this.editClickedListeners = this.editClickedListeners.filter((curr) => {
                return listener != curr;
            })
        }

        private notifyEditClicked(event: MouseEvent) {
            this.editClickedListeners.forEach((listener) => {
                listener(event);
            })
        }

        onSiteConfigFormDisplayed(listener: {(applicationKey: ApplicationKey): void;}) {
            this.siteConfigFormDisplayedListeners.push(listener);
        }

        unSiteConfigFormDisplayed(listener: {(applicationKey: ApplicationKey): void;}) {
            this.siteConfigFormDisplayedListeners =
            this.siteConfigFormDisplayedListeners.filter((curr) => (curr != listener));
        }

        private notifySiteConfigFormDisplayed(applicationKey: ApplicationKey) {
            this.siteConfigFormDisplayedListeners.forEach((listener) => listener(applicationKey));
        }
    }
}