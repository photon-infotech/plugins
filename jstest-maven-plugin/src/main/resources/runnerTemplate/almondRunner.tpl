<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>JsTest Runner</title>
    <script type="text/javascript">
            exports = window;
            QUnit = exports;
    </script>   
    
    <script type="text/javascript" src="/runnerResource/jstest-common.js"></script>
    <!-- test Resources...... -->
    $testResources$

    <!-- sources...... -->
    $sources$

    <!-- tests...... -->
    $tests$
    
</head>
<body>
    <script type="text/javascript">

        var testManager = new TestManager($debug$, $serverMode$, $browserId$, "$runId$", $emulator$);
        testManager.run();
        
    </script>
	<div id='testresult' style="display:none;"></div>
</body>
</html>
