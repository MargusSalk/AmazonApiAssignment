/**
 * 
 */
$(function() {
	$('#searchForm').on('submit', function() {
		var searchTerm = $("#keyword").val();
		var convertedKeyword = convertKeyword(searchTerm);
		var data = {};
		$.ajax({
			type: "GET",
			url: "search/"+convertedKeyword,
			contentType: 'application/json',
			success: function(data) {
				console.log("SUCCESS: ", data);
			},
			error: function(e) {
				console.log("ERROR: ", e);
			},
			done: function(e) {
				console.log("DONE");
			}
		});
		event.preventDefault();
	});
		

	var convertKeyword = function(keyword) {
		return keyword.replace(/ /g, ',');
	}
});