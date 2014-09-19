var TestManager = (function() {
	"use strict";

	return function(debug, isServerMode, browserId, runId, emulator) {
		var serverMode = isServerMode;
//		var myBrowserId = browserId;
		
		this.run = function() {
			var env = jasmine.getEnv();
			var HtmlReport = function() {

			};
			HtmlReport.prototype = new jasmine.HtmlReporter({
				env: env,
				createElement: function() { return document.createElement.apply(document, arguments); }
			});
			HtmlReport.prototype.reportRunnerResults = function(runner) {
				jasmine.HtmlReporter.prototype.reportRunnerResults
						.call(this, runner);
				var results = runner.results();
				var newFavicon = (results.failedCount > 0) ? "favicon-fail.ico"
						: "favicon-success.ico";
				changeFavicon(newFavicon);
			}
			HtmlReport.prototype.reportSpecResults = function(spec) {
				jasmine.HtmlReporter.prototype.reportSpecResults.call(this, spec);
			}
			HtmlReport.prototype.reportSuiteResults = function(suite) {
				jasmine.HtmlReporter.prototype.reportSuiteResults.call(this, suite);
			}
			HtmlReport.prototype.reportSpecStarting = function(spec) {
				jasmine.HtmlReporter.prototype.reportSpecStarting.call(this, spec);
			}
			
			var runStartTime;
			var suiteStartTime;
			var ApiReport = function() {
				
			};
			ApiReport.prototype = new jasmine.JsApiReporter({
				timer: new jasmine.Timer()
			});

			ApiReport.prototype.jasmineStarted = function() {
				runStartTime = new Date().getTime();
				suiteStartTime = runStartTime;
			}

			ApiReport.prototype.jasmineDone = function() {
				var runResult = {};
				JSCOV.storeCurrentRunResult('jasmineRun');
				runResult.coverageResult = JSCOV.getStoredRunResult()[0];
				runResult.duration = new Date().getTime() - runStartTime;
				xmlhttpPost(generateUrl('result/run', browserId, emulator), runResult);
				
				setInterval(function() {
					xmlhttpPost('runId', null, function(serverRunId) {
						if (serverRunId !== '' && runId !== serverRunId) {
							window.location.reload(true);
						}
					});
				}, 500);
			}
			
			if (!serverMode) {
				window.reporter = new ApiReport();
			} else {
				window.reporter = new HtmlReport();
			}
			jasmine.getEnv().addReporter(reporter);
			// jasmine.getEnv().execute();
		};

	};

})();