module api.liveedit.text {

    import ItemTypeConfigJson = api.liveedit.ItemTypeConfigJson;
    import PageComponentItemType = api.liveedit.PageComponentItemType;
    import RegionView = api.liveedit.RegionView;
    import TextComponent = api.content.page.text.TextComponent;

    export class TextItemType extends PageComponentItemType {

        private static INSTANCE = new TextItemType();

        static get(): TextItemType {
            return TextItemType.INSTANCE;
        }

        constructor() {
            super("text", <ItemTypeConfigJson>{
                cssSelector: '[data-live-edit-type=text]',
                draggable: true,
                cursor: 'move',
                iconCls: 'live-edit-font-icon-text',
                highlighterStyle: {
                    stroke: 'rgba(68, 68, 68, 1)',
                    strokeDasharray: '5 5',
                    fill: 'rgba(255, 255, 255, 0)'
                },
                contextMenuConfig: ['parent', 'edit', 'remove', 'clear', 'duplicate']
            });
        }

        createView(parentRegionView: RegionView, textComponent: TextComponent, element?: HTMLElement, dummy?: boolean): TextView {
            return new TextView(parentRegionView, textComponent, element);
        }

        isPageComponentType(): boolean {
            return true
        }
    }

    TextItemType.get();
}