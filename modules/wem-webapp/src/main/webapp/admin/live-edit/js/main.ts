declare var Ext: Ext_Packages;
declare var Admin;
declare var CONFIG;

declare var $liveEdit;

var siteTemplate: api.content.site.template.SiteTemplate;
var content: api.content.Content;

function initializeLiveEdit() {
    //TODO: Maybe move/make more generic
    $('[data-live-edit-empty-component="true"]').each((index, element) => {
        var type = $(element).data('live-edit-type');
        var path = $(element).data('live-edit-component');
        console.log("found empty component", type, path);
        var newEl;
        if (type === "image") {
            newEl = new LiveEdit.component.ImagePlaceholder();
        } else if (type === "part") {
            newEl = new LiveEdit.component.PartPlaceholder();
        } else if (type === "layout") {
            newEl = new LiveEdit.component.LayoutPlaceholder();
        }
        newEl.setComponentPath(path);
        $(element).replaceWith(newEl.getHTMLElement());
    });
}

function getComponentByPath(path:string): LiveEdit.component.Component {
    return LiveEdit.component.Component.fromJQuery($('[data-live-edit-component="'+ path +'"]'));
}

(function ($) {
    'use strict';

    $(window).load(() => {
        new LiveEdit.component.mouseevent.Page();
        new LiveEdit.component.mouseevent.Region();
        new LiveEdit.component.mouseevent.Layout();
        new LiveEdit.component.mouseevent.Part();
        new LiveEdit.component.mouseevent.Image();
        new LiveEdit.component.mouseevent.Paragraph();
        new LiveEdit.component.mouseevent.Content();

        new LiveEdit.component.helper.ComponentResizeObserver();

        new LiveEdit.ui.Highlighter();
        new LiveEdit.ui.ToolTip();
        new LiveEdit.ui.Cursor();
        new LiveEdit.ui.contextmenu.ContextMenu();
        new LiveEdit.ui.Shader();
        new LiveEdit.ui.Editor();

        LiveEdit.component.dragdropsort.DragDropSort.init();

        $(window).resize(() => $(window).trigger('resizeBrowserWindow.liveEdit'));

        $(window).unload(() => console.log('Clean up any css classes etc. that live edit / sortable has added'));
    });

//    Prevent the user from clicking on things
//    This needs more work as we want them to click on Live Edit ui stuff.
    $(document).ready(() => {
        $(document).on('mousedown', 'btn, button, a, select, input', (event) => {
            event.preventDefault();
        });
    });

}($liveEdit));
