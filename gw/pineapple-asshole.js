if (jQuery.when.all===undefined) {
    jQuery.when.all = function(deferreds) {
        var deferred = new jQuery.Deferred();
        $.when.apply(jQuery, deferreds).then(
            function() {
                deferred.resolve(Array.prototype.slice.call(arguments));
            },
            function() {
                deferred.fail(Array.prototype.slice.call(arguments));
            });

        return deferred;
    }
}

firstJobs = [];
finalJobs = [];

function getUpgradeCallback(item, itemData, remaining) {
	return function(upgradeData) {
		document.write('Need '+remaining+' more '+itemData.name+'s for '+upgradeData.name+'<br/>');
	};
}

function getItemCallback(item) {
	return function(itemData) {
		for (i = 0; i < item.needed_by.length; i++) {
			var remaining = item.needed_by[i].count - item.count;
			
			if (remaining == 0) continue;
			
			var upgradeUrl = 'https://api.guildwars2.com/v2/guild/upgrades/'+item.needed_by[i].upgrade_id
			finalJobs.push($.getJSON(upgradeUrl, getUpgradeCallback(item, itemData, remaining)));
		}
	};
}

$.getJSON('https://api.guildwars2.com/v2/guild/ED916952-E34F-49BF-A68F-D64E48EB335D/treasury?access_token=1AE7FEF6-F653-9F4F-A799-DBB7CD16A5DBF46E72FB-AB3A-4619-970D-43C26A1F4DE3', function(data) {
	document.write("~~Pineapple Asshole Summary~~</br></br></br>");
	
	var xxx = [];
	
	for (i = 0; i < data.length; i++) {
		var item = data[i];
		
		var itemUrl = 'https://api.guildwars2.com/v2/items/'+item.item_id
		
		console.log(itemUrl);
		
		var numNeeded = item.count;
		
		firstJobs.push($.getJSON(itemUrl, getItemCallback(item)));
		
		
		//document.write(JSON.stringify(item)+"</br></br>");
	}
	
	$.when.all(firstJobs).then(function() {
		$.when.all(finalJobs).then(function() {
			document.write('<br/><br/>Summary complete!</br>');
		});
    });
	
});


document.write("Thinking....");