(function () {
    // Class definition (constructor)
    var componentMenu = AdminLiveEdit.ui2.ComponentMenu = function () {
        this.buttons = [];
        this.buttonConfig = {
            'page'      : ['settings'],
            'region'    : ['parent', 'insert', 'reset', 'empty'],
            'window'    : ['parent', 'drag', 'settings', 'remove'],
            'content'   : ['parent', 'view', 'edit'],
            'paragraph' : ['parent', 'edit']
        };

        this.create();
        this.registerSubscribers();
    };


    // Inherits ui.Base.js
    componentMenu.prototype = new AdminLiveEdit.ui2.Base();

    // Fix constructor as it now is Base
    componentMenu.constructor = componentMenu;

    // Shorthand ref to the prototype
    var p = componentMenu.prototype;

    // Uses
    var util = AdminLiveEdit.Util;


    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *


    p.registerSubscribers = function() {
        var self = this;
        $liveedit.subscribe('/page/component/select', function (event, $component) {
            self.show.call(self, event, $component);
        });
        $liveedit.subscribe('/page/component/deselect', function() {
            self.hide.call(self);
        });
        $liveedit.subscribe('/page/component/sortstart', function() {
            self.call.fadeOutAndHide();
        });
    };


    p.create = function () {
        this.createElement('<div class="live-edit-context-menu" style="top:-5000px; left:-5000px;">' +
                           '    <div class="live-edit-context-menu-inner"></div>' +
                           '</div>');
        this.appendTo($liveedit('body'));
        this.addButtons();
    };


    p.show = function (event, $component) {
        var componentType = util.getTypeFromComponent($component);
        this.getMenuForComponent(componentType);
        this.moveToComponent($component);
    };


    p.hide = function () {
        this.getEl().css({ top: '-5000px', left: '-5000px', right: '' });
    };


    p.moveToComponent = function($component) {
        var componentBoxModel = util.getBoxModel($component);
        var menuTopPos = Math.round(componentBoxModel.top),
            menuLeftPos = Math.round(componentBoxModel.left + componentBoxModel.width),
            documentSize = util.getDocumentSize();

        if (menuLeftPos >= documentSize.width) {
            menuLeftPos = menuLeftPos - this.getEl().width();
        }

        this.getEl().css({
            top: menuTopPos,
            left: menuLeftPos
        });
    };


    p.getMenuForComponent = function (componentType) {
        if (this.buttonConfig.hasOwnProperty(componentType)) {
            var buttonArray = this.buttonConfig[componentType];
            var buttons = this.getButtons();

            for (var i = 0; i < buttons.length; i++) {
                var $button = buttons[i].getEl();
                var id = $button.attr('data-live-edit-cmp-id');
                var subStr = id.substring(id.lastIndexOf('-') + 1, id.length);
                if (buttonArray.indexOf(subStr) > -1) {
                    $button.show();
                } else {
                    $button.hide();
                }
            }
        }
    };


    p.getButtons = function () {
        return this.buttons;
    };


    p.addButtons = function () {
        var self = this;

        var parentButton = new AdminLiveEdit.ui2.Button();
        parentButton.create({
            id: 'live-edit-button-parent',
            text: 'Parent',
            iconCls: 'live-edit-icon-parent',
            handler: function (event) {
                event.stopPropagation();
                $liveedit.publish('/page/component/select-parent');
            }
        });
        self.buttons.push(parentButton);


        var insertButton = new AdminLiveEdit.ui2.Button();
        insertButton.create({
            text: 'Insert',
            id: 'live-edit-button-insert',
            iconCls: 'live-edit-icon-insert',
            handler: function (event) {
                event.stopPropagation();
            }
        });
        self.buttons.push(insertButton);

        var resetButton = new AdminLiveEdit.ui2.Button();
        resetButton.create({
            text: 'Reset',
            id: 'live-edit-button-reset',
            iconCls: 'live-edit-icon-reset',
            handler: function (event) {
                event.stopPropagation();
            }
        });
        self.buttons.push(resetButton);


        var emptyButton = new AdminLiveEdit.ui2.Button();
        emptyButton.create({
            text: 'Empty',
            id: 'live-edit-button-empty',
            iconCls: 'live-edit-icon-empty',
            handler: function (event) {
                event.stopPropagation();
            }
        });
        self.buttons.push(emptyButton);


        var viewButton = new AdminLiveEdit.ui2.Button();
        viewButton.create({
            text: 'View',
            id: 'live-edit-button-view',
            iconCls: 'live-edit-icon-view',
            handler: function (event) {
                event.stopPropagation();
            }
        });
        self.buttons.push(viewButton);


        var editButton = new AdminLiveEdit.ui2.Button();
        editButton.create({
            text: 'edit',
            id: 'live-edit-button-edit',
            iconCls: 'live-edit-icon-edit',
            handler: function (event) {
                event.stopPropagation();
            }
        });
        self.buttons.push(editButton);


        var settingsButton = new AdminLiveEdit.ui2.Button();
        settingsButton.create({
            text: 'Settings',
            id: 'live-edit-button-settings',
            iconCls: 'live-edit-icon-settings',
            handler: function (event) {
                event.stopPropagation();
            }
        });
        self.buttons.push(settingsButton);

        var dragButton = new AdminLiveEdit.ui2.Button();
        dragButton.create({
            text: 'Drag',
            id: 'live-edit-button-drag',
            iconCls: 'live-edit-icon-drag',
            handler: function (event) {
                event.stopPropagation();
            }
        });
        dragButton.getEl().on('mousemove', function (event) {
            if (this._mouseDown) {
                this._mouseDown = false;
                self.fadeOutAndHide();
                var highlighter = AdminLiveEdit.ui.Highlighter;
                var $selectedComponent = highlighter.getSelected();
                var evt = document.createEvent('MouseEvents');
                evt.initMouseEvent('mousedown', true, true, window, 0, event.screenX, event.screenY, event.clientX, event.clientY, false,
                    false, false, false, 0, null);

                $selectedComponent[0].dispatchEvent(evt);
            }
        });
        dragButton.getEl().on('mouseup', function (event) {
            this._mouseDown = false;
            AdminLiveEdit.ui.DragDrop.disable();
        });
        self.buttons.push(dragButton);


        var removeButton = new AdminLiveEdit.ui2.Button();
        removeButton.create({
            text: 'Remove',
            id: 'live-edit-button-remove',
            iconCls: 'live-edit-icon-remove',
            handler: function (event) {
                event.stopPropagation();
            }
        });
        self.buttons.push(removeButton);

        for (var i = 0; i < self.buttons.length; i++) {
            self.buttons[i].appendTo(self.getEl());
        }
    };


    p.fadeOutAndHide = function () {
        this.getEl().fadeOut(500, function () {
            $liveedit.publish('/page/component/deselect');
        });
    };


}());