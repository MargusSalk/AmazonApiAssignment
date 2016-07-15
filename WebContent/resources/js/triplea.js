/**
 * 
 */
$(function() {

	var itemsPerPageConst = 13;
	var apiLimit = 50;
	var currentPage = 1;
	// 2 dimensional array; contains arrays of items
	var pages = [];
	// If there's extra items after populating the loaded page then they are
	// stored here and later added to the next page if needed.
	var extraItems = [];
	var convertedKeyword = "";

	var currencyMultiplier = 1;
	var currencyRates = {};

	// The currencyRates are loaded upon loading the page
	$.ajax({
		type : "GET",
		url : "http://api.fixer.io/latest?base=USD",
		contentType : "application/json",
		success : function(result) {
			currencyRates = result.rates;
			populateCurrencySelect(currencyRates);
		},
		error : function(e) {
			console.log("Currency JSON error: " + e);
		}
	});

	var populateCurrencySelect = function(currencyRates) {
		var select = $("#currency-select");
		for ( var currency in currencyRates) {
			if (currencyRates.hasOwnProperty(currency)) {
				select.append($("<option>", {
					value : currencyRates[currency]
				}).text(currency));
			}
		}
		// Adds USD as an option and selects it as a default
		select.append($("<option>", {
			value : 1
		}).text("USD"));
		select.val(1).change();
	}

	$("#currency-select").change(function() {
		currencyMultiplier = $("#currency-select option:selected").val();
		updatePrices();
	});
	
	var updatePrices = function() {
		$(".price").each(function(i, obj) {
			var newPrice = Math.round(($(this).data('price') * currencyMultiplier) * 100) / 100;
			$(this).text(newPrice);
		});
	}

	// Submitting the original query, also preloads the second page.
	$('#searchForm').on('submit', function() {
		reset();
		var keyword = $("#keyword").val();
		if (keyword != "") {
			var searchTerm = $("#keyword").val();
			convertedKeyword = convertKeyword(searchTerm);
			var data = {};
			$.ajax({
				type : "GET",
				url : "search/" + convertedKeyword,
				contentType : 'application/json',
				success : function(data) {
					parseFirstData(data);
				},
				error : function(e) {
					console.log("ERROR: ", e);
				},
				done : function(e) {
					console.log("DONE");
				}
			});
		}
		event.preventDefault();
	});

	var parseFirstData = function(data) {
		if (data) {
			var items = JSON.parse(data);
			divideItemsToPages(items);
			showFirstPage(pages[0]);
		}
	}

	var showFirstPage = function(items) {
		var html = "";
		html = createHtml(items, 1);
		if (html != "") {
			$("#table-data").append(html);
			$("#table").hide().removeClass("hidden");
			var lastPage = Math.ceil(apiLimit / itemsPerPageConst);
			if (lastPage === 2) {
				pages.push(extraItems);
			}
			if (!pages[1]) {
				$("#next-page").addClass("disabled");
			} else {
				$("#next-page").removeClass("disabled");
			}
			$("#previous-page").addClass("disabled");
			$("#page-number").text(currentPage);
			updatePrices();
			$("#table").slideDown(800);
		}
	}

	$("#next-page").click(function() {
		var page = getNextPageNumber();
		renderPage(page);
	});

	$("#previous-page").click(function() {
		var page = getPreviousPageNumber();
		renderPage(page);
	});

	var renderPage = function(page) {
		items = pages[page - 1];
		if (items) {
			var html = createHtml(items, page);
			if (html != "") {
				$("#table-data").empty();
				$("#table-data").append(html);
				updatePrices();
				setCurrentPage(page);
				navButtonToggle(page);
				
				$("#page-number").text(page);
			}
			if (items.length === itemsPerPageConst && !pages[page]) {
				preLoadNextPage(page + 1);
			}
		}
	}

	var getNextPageNumber = function() {
		return currentPage + 1;
	}

	var getPreviousPageNumber = function() {
		return currentPage - 1;
	}

	var setCurrentPage = function(page) {
		currentPage = page;
	}

	//
	var preLoadNextPage = function(nextPage) {
		if (convertedKeyword != "") {
			var data = {};
			$.ajax({
				type : "GET",
				url : "search/" + nextPage + "/" + convertedKeyword,
				contentType : 'application/json',
				success : function(data) {
					var items = parseData(data);
					divideItemsToPages(items);
					// On last page it should just dump all the extra items into
					// the array
					var lastPage = Math.ceil(apiLimit / itemsPerPageConst);
					if (lastPage === nextPage
							|| (items.length === 0 && extraItems)) {
						pages.push(extraItems);
					}
					if (pages[currentPage]) {
						$("#next-page").removeClass("disabled");
					}
				},
				error : function(e) {
					console.log("ERROR: ", e);
				}
			});
		}
	}

	var parseData = function(data) {
		if (data) {
			return JSON.parse(data);
		}
		return [];
	}

	var navButtonToggle = function(page) {
		var previous = $("#previous-page");
		var next = $("#next-page");
		if (!pages[page - 2]) {
			previous.addClass("disabled");
		} else {
			previous.removeClass("disabled");
		}
		if (!pages[page]) {
			next.addClass("disabled");
		} else {
			next.removeClass("disabled");
		}
	}

	var createHtml = function(items, pageRendered) {
		var len = items.length;
		setCurrentPage(pageRendered);
		var html = "";
		if (len > 0) {
			for (var i = 0; i < len; i++) {
				// If the API returns 0 as a price, replaces the price it with
				// '-'
				// Mostly the case for ebooks.
				var price = items[i].price != 0.00 ? ("<div class='price' data-price="
						+ items[i].price + " " + ">" + items[i].price + "</div>")
						: "-";
				//
				html += "<tr class='clickable-row' onclick=\"goTo('"
						+ items[i].url + "');\"><td>" + items[i].title
						+ " <div class='product-group'>"
						+ items[i].productGroup + "</div></td><td>";
				html += price
				html += "</td></tr>";
			}
		}
		return html;
	}

	var reset = function() {
		$("#table-data").empty();
		$("#table").addClass("hidden");
		currentPage = 1;
		pages = [];
		extraItems = [];
		convertedKeyword = "";
	}

	// divides the items into arrays (array's length is variable
	// 'itemsPerPageConst') and adds them to array (global variable 'pages').
	// 
	var divideItemsToPages = function(items) {
		var tempItems = [];
		// if there's extra items, add them to the start of the new page
		if (extraItems != []) {
			tempItems = tempItems.concat(extraItems);
			extraItems = [];
		}
		// adds items into a temporary array until it's length is
		// 'itemsPerPageConst' and then adds it as a new page to 'pages'
		for (var i = 0; i < items.length; i++) {
			tempItems.push(items[i]);
			if (tempItems.length === itemsPerPageConst) {
				pages.push(tempItems);
				tempItems = [];
			}
		}
		// items from incomplete page will be added to 'extraItems'
		if (tempItems != []) {
			if (pages.length === 0) {
				pages.push(tempItems);
			} else {
				extraItems = tempItems;
			}
		}
	}
	// replaces spaces with commas to make the whole query word readable for
	// Amazon Product Advertisement API
	var convertKeyword = function(keyword) {
		return keyword.replace(/ /g, ',');
	}

});

var goTo = function(url) {
	window.open(url, '_blank');
}