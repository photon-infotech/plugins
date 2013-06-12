/*
 * Phresco Maven Plugin
 *
 * Copyright (C) 1999-2013 Photon Infotech Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
YUI 3.4.1 (build 4118)
Copyright 2011 Yahoo! Inc. All rights reserved.
Licensed under the BSD License.
http://yuilibrary.com/license/
*/
YUI.add('widget-base-ie', function(Y) {

/**
 * IE specific support for the widget-base module.
 *
 * @module widget-base-ie
 */
var BOUNDING_BOX = "boundingBox",
    CONTENT_BOX = "contentBox",
    HEIGHT = "height",
    OFFSET_HEIGHT = "offsetHeight",
    EMPTY_STR = "",
    IE = Y.UA.ie,
    heightReallyMinHeight = IE < 7,
    bbTempExpanding = Y.Widget.getClassName("tmp", "forcesize"),
    contentExpanded = Y.Widget.getClassName("content", "expanded");

// TODO: Ideally we want to re-use the base _uiSizeCB impl
Y.Widget.prototype._uiSizeCB = function(expand) {

    var bb = this.get(BOUNDING_BOX),
        cb = this.get(CONTENT_BOX),
        borderBoxSupported = this._bbs;

    if(borderBoxSupported === undefined) {
        this._bbs = borderBoxSupported = !(IE < 8 && bb.get("ownerDocument").get("compatMode") != "BackCompat"); 
    }

    if (borderBoxSupported) {
        cb.toggleClass(contentExpanded, expand);
    } else {
        if (expand) {
            if (heightReallyMinHeight) {
                bb.addClass(bbTempExpanding);
            }

            cb.set(OFFSET_HEIGHT, bb.get(OFFSET_HEIGHT));

            if (heightReallyMinHeight) {
                bb.removeClass(bbTempExpanding);
            }
        } else {
            cb.setStyle(HEIGHT, EMPTY_STR);
        }
    }
};


}, '3.4.1' ,{requires:['widget-base']});
