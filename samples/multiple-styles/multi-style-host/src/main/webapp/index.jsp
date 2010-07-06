<html>
	<head>
		<title>SpringSource Snaps Pluggable Styling Demonstration</title>
		
		<link rel="stylesheet" href="default-style/main.css" title="Default" type="text/css" />
		<link rel="alternate stylesheet" href="plain-style/main.css" title="Plain" type="text/css" />	
		<link rel="alternate stylesheet" href="green-style/main.css" title="Green" type="text/css" />	
		
		<script lang="javascript">
			function setStyle(title) {
				links = document.getElementsByTagName('link');
				for (var i = 0; i < links.length; i++) {
					if (links[i].getAttribute('rel').indexOf('style') > -1 && links[i].getAttribute('title')) {
						if (links[i].getAttribute('title') == title) {
							links[i].disabled = false;
						} else {
							links[i].disabled = true;
						}			
					}
				}
			}			
		</script>
	</head>
	<body>
		<div class="header">
			<div class="title">SpringSource Snaps</div>
			<div class="subtitle">Multiple Styles Demonstration</div>
		</div>
		<div class="style-selection">
			<select>
				<option onclick="setStyle('Default')">Default</option>
				<option onclick="setStyle('Plain')">Plain</option>
				<option onclick="setStyle('Green')">Green</option>
			</select>
		</div>
	</body>
</html>
