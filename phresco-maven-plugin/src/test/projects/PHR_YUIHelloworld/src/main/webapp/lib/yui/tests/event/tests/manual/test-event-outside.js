/*
 * Phresco Maven Plugin
 *
 * Copyright (C) 1999-2014 Photon Infotech Inc.
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
YUI({ filter: 'raw' }).use('gallery-event-konami', 'event-focus', 'event-outside', function (Y) {
    var selectors = [
            'blur','change','click','dblclick','focus','keydown','keypress',
            'keyup','mousedown','mousemove','mouseout','mouseover','mouseup',
            'select','submit','konami'
        ],
        timers = {};
    
    function setup(selector) {
        var node = Y.one('#' + selector);
        
        node.on(selector + 'outside', function (e) {
            clearTimeout(timers[selector]);
            
            var t    = e.target,
                id   = t.get('id'),
                text = t.get('tagName').toLowerCase() + (id ? '#' + id : '');
            
            this.addClass('outside');
            this.one('span').set('innerHTML', text);
            
            if (id === 'link' || selector === 'submit') {
                e.preventDefault();
            }
            
            timers[selector] = setTimeout(function () {
                node.removeClass('outside');
                node.one('span').set('innerHTML', '');
            }, 700);
        });
    }
    
    Y.Event.defineOutside('konami');
    
    Y.each(selectors, setup);
});
