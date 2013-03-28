var TestManager = (function() {
	"use strict";
	
	return function(debug, isServerMode, browserId, runId, emulator) {
		
		this.run = function(TestRunner, YTest) {
			TestRunner.subscribe(TestRunner.COMPLETE_EVENT, function() {
				var resultsXML = TestRunner.getResults(YTest.Format.JUnitXML);
				var xmlDoc = loadXMLDoc(resultsXML);
				var testSuites = xmlDoc.documentElement.childNodes;
				for (var i = 0; i < testSuites.length; i++) {
					var testSuite = testSuites[i];
					var testCases = testSuite.childNodes;
					var jsonSuite = {tests : []};
					jsonSuite.name = testSuite.getAttribute('name');
					for (var j = 0; j < testCases.length; j++) {
						var testCase = {};
						var failure = {};
						testCase.name = testCases[j].getAttribute('name');
						testCase.duration = Number(testCases[j].getAttribute('time')) * 1000;	
						var failueNode = testCases[j].childNodes;
						if(failueNode.length != 0){
							for(var k = 0; k < failueNode.length; k++){
								testCase.failure = {};
								testCase.failure.message = failueNode[k].getAttribute('message');
							}
						}
						jsonSuite.tests[j] = testCase;
					}
					jsonSuite.duration = Number(testSuite.getAttribute('time'))* 1000;
					xmlhttpPost(generateUrl('result/suite', browserId, emulator)+'&testRunner=yui', jsonSuite);
				}

				var codecoverage = TestRunner.getCoverage(YTest.CoverageFormat.JSON);
				xmlhttpPost(generateUrl('result/run', browserId, emulator)+'&testRunner=yui', JSON.parse(codecoverage));
			});
			TestRunner.run();
		};
	};
	
	function loadXMLDoc(xmlString) {
		var xmldoc;
		if (window.DOMParser) {
			var parser=new DOMParser();
			var xmlDoc=parser.parseFromString(xmlString,"text/xml");
		}
		else { // Internet Explorer 
			var xmlDoc=new ActiveXObject("Microsoft.XMLDOM");
			xmlDoc.async=false;
			xmlDoc.loadXML(xmlString);
		}  
		return xmlDoc;
	}
})();