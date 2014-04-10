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
YUI.add('datatable-sort-tests', function(Y) {

var suite = new Y.Test.Suite("Y.Plugin.DataTableSort");

suite.add(new Y.Test.Case({
    name: "DataTableSort tests",

    "datatable-sort should not rely on a link in the template": function () {
        var table, link, th;
        
        table = new Y.DataTable.Base({
            columnset: [{ key: 'a', sortable: true }],
            recordset: [{ a: "a1" }, { a: "a2" }, { a: "a3" }]
        }).plug(Y.Plugin.DataTableSort);

        Y.one('#testbed').empty();

        table.render('#testbed');

        th = table._theadNode.one('th');

        Y.Assert.isInstanceOf(Y.Node, th);

        link = th.one('a');
        Y.Assert.isInstanceOf(Y.Node, link);

        // Should not error
        link.simulate('click');

        table.destroy();

        Y.one('#testbed').empty();

        table = new Y.DataTable.Base({
            columnset: [{ key: 'a', sortable: true }],
            recordset: [{ a: "a1" }, { a: "a2" }, { a: "a3" }]
        }).plug(Y.Plugin.DataTableSort, {
            trigger: {
                selector: 'th',
                event: 'click'
            },
            template: '{value}' // override the template with link
        });

        table.render('#testbed');

        th = table._theadNode.one('th');

        Y.Assert.isInstanceOf(Y.Node, th);

        link = th.one('a');
        Y.Assert.isNull(link);

        // Should not error
        th.simulate('click');

        table.destroy();

        Y.one('#testbed').empty();
    }

    // test direction classes
    // test trigger event
    // test unplug
}));

Y.Test.Runner.add(suite);


}, '@VERSION@' ,{requires:['datatable-sort', 'test', 'node-event-simulate']});
