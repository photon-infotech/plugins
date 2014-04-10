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
var suite = new Y.Test.Suite("Y.JSONPRequest and Y.jsonp");

suite.add(new Y.Test.Case({
    name : "Callback in URL",

    "callback in URL should be executed": function () {
        var self = this;

        Y.config.win.globalFunction = function (json) {
            self.resume(function () {
                Y.config.win.globalFunction = undefined;
                Y.Assert.isObject(json);
            });
        };
        Y.jsonp("server/service.php?&callback=globalFunction");

        self.wait();
    },
        
    "inline callback should be replaced if function passed": function () {
        var self = this;

        Y.config.win.globalFunction = function (json) {
            self.resume(function () {
                Y.config.win.globalFunction = undefined;
                Y.Assert.fail("inline function should not be used");
            });
        };

        Y.jsonp("server/service.php?&callback=globalFunction", function (data) {
            self.resume(function () {
                Y.config.win.globalFunction = undefined;
                Y.Assert.isObject(data);
            });
        });

        self.wait();
    },
        
    "inline callback should be replaced if success function provided in config": function () {
        var self = this;

        Y.config.win.globalFunction = function (json) {
            self.resume(function () {
                Y.config.win.globalFunction = undefined;
                Y.Assert.fail("inline function should not be used");
            });
        };

        Y.jsonp("server/service.php?&callback=globalFunction", {
            on: {
                success: function (data) {
                    self.resume(function () {
                        Y.config.win.globalFunction = undefined;
                        Y.Assert.isObject(data);
                    });
                }
            }
        });

        self.wait();
    },
    
    "complex nested callback in URL should be executed": function () {
        var self = this;

        Y.config.win.deeply = [
            null,
            null,
            {
                nested: {
                    global: {
                        func: {
                            tion: function (json) {
                                self.resume(function () {
                                    Y.config.win.deeply = undefined;
                                    Y.Assert.isObject(json);
                                });
                            }
                        }
                    }
                }
            }
        ];

        Y.jsonp('server/service.php?&callback=deeply[2].nested["global"].func["tion"]');

        self.wait();
    },

    "callback relative to Y should be executed": function () {
        var self = this;

        Y.callbackFunction = function (json) {
            self.resume(function () {
                delete Y.callbackFunction;
                Y.Assert.isObject(json);
            });
        };
        Y.jsonp("server/service.php?&callback=callbackFunction");

        self.wait();
    },

    "nested inline callback relative to Y should be executed": function () {
        var self = this;

        Y.deeply = [
            null,
            null,
            {
                nested: {
                    global: {
                        func: {
                            tion: function (json) {
                                self.resume(function () {
                                    delete Y.deeply;
                                    Y.Assert.isObject(json);
                                });
                            }
                        }
                    }
                }
            }
        ];

        Y.jsonp('server/service.php?&callback=deeply[2].nested["global"].func["tion"]');
        self.wait();
    },

    "inline callback including 'Y.' should be executed": function () {
        var self = this;

        Y.callbackFunction = function (json) {
            self.resume(function () {
                delete Y.callbackFunction;
                Y.Assert.isObject(json);
            });
        };
        Y.jsonp("server/service.php?&callback=Y.callbackFunction");

        self.wait();
    },

    "nested inline callback should be replaced if function passed": function () {
        var self = this;

        Y.deeply = [
            null,
            null,
            {
                nested: {
                    global: {
                        func: {
                            tion: function (json) {
                                self.resume(function () {
                                    delete Y.deeply;
                                    Y.Assert.fail("inline function should not be used");
                                });
                            }
                        }
                    }
                }
            }
        ];

        Y.jsonp('server/service.php?&callback=deeply[2].nested["global"].func["tion"]', function (data) {
            self.resume(function () {
                delete Y.deeply;
                Y.Assert.isObject(data);
            });
        });

        self.wait();
    },

    "nested inline callback should be replaced if success function provided in config": function () {
        var self = this;

        Y.deeply = [
            null,
            null,
            {
                nested: {
                    global: {
                        func: {
                            tion: function (json) {
                                self.resume(function () {
                                    delete Y.deeply;
                                    Y.Assert.fail("inline function should not be used");
                                });
                            }
                        }
                    }
                }
            }
        ];

        Y.jsonp('server/service.php?&callback=deeply[2].nested["global"].func["tion"]', {
            on: {
                success: function (data) {
                    self.resume(function () {
                        delete Y.deeply;
                        Y.Assert.isObject(data);
                    });
                }
            }
        });

        self.wait();
    }

}));

Y.Test.Runner.add(suite);
