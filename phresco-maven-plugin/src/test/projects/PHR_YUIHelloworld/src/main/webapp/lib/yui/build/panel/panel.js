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
YUI.add('panel', function(Y) {

/**
 * Provides a Panel widget, a widget that mimics the functionality of a regular OS window.
 * Comes with Standard Module support, XY Positioning, Alignment Support, Stack (z-index) support,
 * modality, auto-focus and auto-hide functionality, and header/footer button support.
 *
 * @module panel
 */

/**
 * A basic Panel Widget, which can be positioned based on Page XY co-ordinates and is stackable (z-index support).
 * It also provides alignment and centering support and uses a standard module format for it's content, with header,
 * body and footer section support. It can be made modal, and has functionality to hide and focus on different events.
 * The header and footer sections can be modified to allow for button support.
 *
 * @class Panel
 * @constructor
 * @extends Widget
 * @uses WidgetStdMod
 * @uses WidgetPosition
 * @uses WidgetStack
 * @uses WidgetPositionAlign
 * @uses WidgetPositionConstrain
 * @uses WidgetModality
 * @uses WidgetAutohide
 * @uses WidgetButtons
 * @param {Object} object The user configuration for the instance.
 */
Y.Panel = Y.Base.create("panel", Y.Widget, [Y.WidgetStdMod, Y.WidgetPosition, Y.WidgetStack, Y.WidgetPositionAlign, Y.WidgetPositionConstrain, Y.WidgetModality, Y.WidgetAutohide, Y.WidgetButtons]);


}, '3.4.1' ,{requires:['widget', 'widget-stdmod', 'widget-position', 'widget-stack', 'widget-position-align', 'widget-position-constrain', 'widget-modality', 'widget-autohide', 'widget-buttons']});
