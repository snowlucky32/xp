/*
 This code contains a lot of prototype coding at the moment.
 A clean up should be done when Live Edit is specked
 */

module LiveEdit.DragDropSort {
    var $ = $liveEdit;

    var componentHelper = LiveEdit.ComponentHelper;

    var _isDragging = false;

    var cursorAt = {left: -10, top: -15};

    var regionSelector = '[data-live-edit-type=region]';

    var layoutSelector = '[data-live-edit-type=layout]';

    var partSelector = '[data-live-edit-type=part]';

    var paragraphSelector = '[data-live-edit-type=paragraph]';

    var sortableItemsSelector = layoutSelector + ',' + partSelector + ',' + paragraphSelector;

    export function init():void {
        this.createSortable();
        this.registerGlobalListeners();
    }

    export function isDragging():bool {
        return _isDragging;
    }

    export function enableDragDrop():void {
        $(regionSelector).sortable('enable');
    }

    export function disableDragDrop():void {
        $(regionSelector).sortable('disable');
    }

    export function createSortable():void {
        $(regionSelector).sortable({
            revert: false,
            connectWith: regionSelector,
            items: sortableItemsSelector,
            distance: 1,
            delay: 150,
            tolerance: 'pointer',
            cursor: 'move',
            cursorAt: cursorAt,
            scrollSensitivity: Math.round(LiveEdit.DomHelper.getViewPortSize().height / 8),
            placeholder: 'live-edit-drop-target-placeholder',
            zIndex: 1001000,
            helper: (event, helper) => this.createDragHelper(event, helper),
            start: (event, ui) => this.handleSortStart(event, ui),
            over: (event, ui) =>  this.handleDragOver(event, ui),
            out: (event, ui) =>  this.handleDragOut(event, ui),
            change: (event, ui) => this.handleSortChange(event, ui),
            receive: (event, ui) =>  this.handleReceive(event, ui),
            update: (event, ui) =>  this.handleSortUpdate(event, ui),
            stop: (event, ui) =>  this.handleSortStop(event, ui)
        });
    }

    // Used by the Context Window when dragging above the IFrame
    export function createDraggable(component:JQuery):void {
        component.draggable({
            connectToSortable: regionSelector,
            addClasses: false,
            cursor: 'move',
            appendTo: 'body',
            zIndex: 5100000,
            cursorAt: cursorAt,
            helper: () => {
                return createDragHelperHtml('');
            },
            start: (event, ui) => {
                $(window).trigger('dragStart.liveEdit.component', [event, ui]);
                this.setDragHelperText($(event.target).data('live-edit-component-name'));
                _isDragging = true;
            },
            stop: (event, ui) => {
                $(window).trigger('dragStop.liveEdit.component', [event, ui]);
                _isDragging = false;
            }
        });
    }

    export function createDragHelperHtml(text:string):string {
        // Override jQueryUi with inline width/height
        return '<div id="live-edit-drag-helper" style="width: 150px; height: 16px;">' +
               '    <div id="live-edit-drag-helper-status-icon" class="live-edit-drag-helper-no"></div>' +
               '    <span id="live-edit-drag-helper-text" style="width: 134px;">' + text + '</span>' +
               '</div>';
    }

    export function createDragHelper(event:JQueryEventObject, helper):string {
        return $(createDragHelperHtml(componentHelper.getComponentName(helper)));
    }

    export function setDragHelperText(text:string):void {
        $('#live-edit-drag-helper-text').text(text);
    }

    export function setHelperStatusIcon(status:string):void {
        $('#live-edit-drag-helper-status-icon').attr('class', 'live-edit-drag-helper-' + status);
    }

    export function refreshSortable():void {
        $(regionSelector).sortable('refresh');
    }

    export function targetIsPlaceholder(target:JQuery):Boolean {
        return target.hasClass('live-edit-drop-target-placeholder')
    }

    export function handleSortStart(event:JQueryEventObject, ui):void {
        _isDragging = true;

        // Temporary store the selection info during the drag drop lifecycle.
        // Data is nullified on drag stop.
        var componentIsSelected = ui.item.hasClass('live-edit-selected-component');
        ui.item.data('live-edit-selected-on-sort-start', componentIsSelected);

        var targetComponentName = LiveEdit.ComponentHelper.getComponentName($(event.target));
        ui.placeholder.html('Drop component here' + '<div style="font-size: 10px;">' + targetComponentName + '</div>');

        this.refreshSortable();

        $(window).trigger('sortStart.liveEdit.component', [event, ui]);
    }

    export function handleDragOver(event:JQueryEventObject, ui):void {
        event.stopPropagation();

        // todo: Items in component should have the same @data-live-edit-* structure
        var draggedItemIsLayoutComponent = ui.item.data('live-edit-component-type') === 'layout' ||
                                           ui.item.data('live-edit-type') === 'layout',
            isDraggingOverLayoutComponent = ui.placeholder.closest(layoutSelector).length > 0;

        if (draggedItemIsLayoutComponent && isDraggingOverLayoutComponent) {
            this.setHelperStatusIcon('no');
            ui.placeholder.hide();
        } else {
            this.setHelperStatusIcon('yes');
            $(window).trigger('sortOver.liveEdit.component', [event, ui]);
        }
    }

    export function handleDragOut(event:JQueryEventObject, ui):void {
        if (this.targetIsPlaceholder($(event.srcElement))) {
            this.removePaddingFromLayoutComponent();
        }
        this.setHelperStatusIcon('no');
        $(window).trigger('sortOut.liveEdit.component', [event, ui]);
    }

    export function handleSortChange(event:JQueryEventObject, ui):void {
        this.addPaddingToLayoutComponent($(event.target));
        this.setHelperStatusIcon('yes');
        ui.placeholder.show(null);
        $(window).trigger('sortChange.liveEdit.component', [event, ui]);
    }

    export function handleSortUpdate(event:JQueryEventObject, ui):void {
        $(window).trigger('sortUpdate.liveEdit.component', [event, ui]);
    }

    export function handleSortStop(event:JQueryEventObject, ui):void {
        _isDragging = false;

        this.removePaddingFromLayoutComponent();

        // todo: Items in component should have the same @data-live-edit-* structure
        var draggedItemIsLayoutComponent = ui.item.data('live-edit-component-type') === 'layout' ||
                                           ui.item.data('live-edit-type') === 'layout',
            targetIsInLayoutComponent = $(event.target).closest(layoutSelector).length > 0;

        if (draggedItemIsLayoutComponent && targetIsInLayoutComponent) {
            ui.item.remove()
        }

        if (LiveEdit.ComponentHelper.supportsTouch()) {
            $(window).trigger('mouseOut.liveEdit.component');
        }

        var wasSelectedOnDragStart = ui.item.data('live-edit-selected-on-drag-start');

        $(window).trigger('sortStop.liveEdit.component', [event, ui, wasSelectedOnDragStart]);

        ui.item.removeData('live-edit-selected-on-drag-start');
    }

    export function handleReceive(event:JQueryEventObject, ui):void {
        console.log('receive')
        if (this.itemIsDraggedFromComponentBar(ui.item)) {
            var $componentBarComponent = $(event.target).children('.live-edit-component'),
                componentKey = $componentBarComponent.data('live-edit-component-key'),
                componentType = $componentBarComponent.data('live-edit-component-type'),
                url = '../../../admin2/live-edit/data/mock-component-' + componentKey + '.html';

            $componentBarComponent.hide();

            $.ajax({
                url: url,
                cache: false
            }).done((html) => {
                    $componentBarComponent.replaceWith(html);
                    // It seems like it is not possible to add new sortables (region in layout) to the existing sortable
                    // So we have to create it again.
                    // Ideally we should destroy the existing sortable first before creating.
                    if (componentType === 'layout') {
                        this.createSortable();
                    }
                    $(window).trigger('sortUpdate.liveEdit.component');
                });
        }
    }

    export function itemIsDraggedFromComponentBar(item:JQuery):Boolean {
        return item.hasClass('live-edit-component');
    }

    export function addPaddingToLayoutComponent(component:JQuery):void {
        component.closest(layoutSelector).addClass('live-edit-component-padding');
    }


    export function removePaddingFromLayoutComponent():void {
        $('.live-edit-component-padding').removeClass('live-edit-component-padding');
    }

    export function registerGlobalListeners():void {
        $(window).on('deselect.liveEdit.component', () => {
            if (LiveEdit.ComponentHelper.supportsTouch() && !_isDragging) {
                this.disableDragDrop();
            }
        });

        $(window).on('paragraphSelect.liveEdit.component', () => {
            $(regionSelector).sortable('option', 'cancel', '[data-live-edit-type=paragraph]');
        });

        $(window).on('paragraphLeave.liveEdit.component', () => {
            $(regionSelector).sortable('option', 'cancel', '');
        });
    }

}