module app_wizard {

    export class RelationshipTypeWizardPanel extends api_app_wizard.WizardPanel {

        private static DEFAULT_CHEMA_ICON_URL: string = '/admin/rest/schema/image/RelationshipType:_:_';

        private saveAction: api_ui.Action;

        private closeAction: api_ui.Action;

        private formIcon: api_app_wizard.FormIcon;

        private toolbar: api_ui_toolbar.Toolbar;

        private persistedRelationshipType: api_remote_relationshiptype.RelationshipType;

        constructor(id: string) {
            this.formIcon = new api_app_wizard.FormIcon(RelationshipTypeWizardPanel.DEFAULT_CHEMA_ICON_URL, "Click to upload icon", "rest/upload");

            this.saveAction = new SaveRelationshipTypeAction();
            this.closeAction = new CloseRelationshipTypeAction(this, true);

            this.toolbar = new RelationshipTypeWizardToolbar({
                saveAction: this.saveAction,
                closeAction: this.closeAction
            });

            super({
                formIcon: this.formIcon,
                toolbar: this.toolbar,
                saveAction: this.saveAction
            });

            this.setDisplayName("New Relationship Type");
            this.setName(this.generateName(this.getDisplayName()));
            this.setAutogenerateDisplayName(true);
            this.setAutogenerateName(true);

            this.addStep(new api_app_wizard.WizardStep("Relationship Type", new RelationshipTypeForm()));
        }

        setPersistedItem(relationshipType: api_remote_relationshiptype.RelationshipType) {
            super.setPersistedItem(relationshipType);

            this.setDisplayName(relationshipType.displayName);
            this.setName(relationshipType.name);
            this.formIcon.setSrc(relationshipType.iconUrl);

            this.setAutogenerateDisplayName(!relationshipType);
            this.setAutogenerateName(!relationshipType.name || relationshipType.name == this.generateName(relationshipType.displayName));

            this.persistedRelationshipType = relationshipType;
        }

        persistNewItem() {
//            var createParams:api_remote.RemoteCallCreateOrUpdateRelationshipTypeParams = {
//                // TODO: set relationshipType xml.
//                relationshipType: "",
//                iconReference: this.getIconUrl()
//            };
//
//            api_remote.RemoteService.relationshipType_createOrUpdate(createParams, () => {
//                new app_wizard.RelationshipTypeCreatedEvent().fire();
//                api_notify.showFeedback('Relationship type was created!');
//            });
        }

        updatePersistedItem() {
//            var updateParams:api_remote.RemoteCallCreateOrUpdateRelationshipTypeParams = {
//                // TODO: set relationshipType xml.
//                relationshipType: "",
//                iconReference: this.getIconUrl()
//            };
//
//            api_remote.RemoteService.relationshipType_createOrUpdate(updateParams, () => {
//                new app_wizard.RelationshipTypeUpdatedEvent().fire();
//                api_notify.showFeedback('Relationship type was saved!');
//            });
        }

        askUserForSaveChangesBeforeClosing() {
            if (!components.wizardSaveDialog) {
                components.wizardSaveDialog = new app_wizard.SchemaWizardSaveDialog();
            }
            components.wizardSaveDialog.setWizardToSave(this).open();
        }
    }
}