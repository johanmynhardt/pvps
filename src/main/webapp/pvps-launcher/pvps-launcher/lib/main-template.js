var baseUrl = "http://localhost:8080/pvps";

chrome.browserAction.onClicked.addListener(
	function(tab) {
		var action_url = baseUrl; //+ encodeURIComponent(tab.url);
		chrome.tabs.create({url: action_url});
	}
);
// vim: tabstop=2
