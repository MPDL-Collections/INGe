	
	/*
	 *	jquery.suggest 1.1 - 2007-08-06
	 *	
	 *	Uses code and techniques from following libraries:
	 *	1. http://www.dyve.net/jquery/?autocomplete
	 *	2. http://dev.jquery.com/browser/trunk/plugins/interface/iautocompleter.js	
	 *
	 *	All the new stuff written by Peter Vulgaris (www.vulgarisoip.com)	
	 *	Feel free to do whatever you want with this file
	 *
	 */
	
	(function($) {

		$.suggest = function(input, options) {
	
			var $input = $(input).attr("autocomplete", "off");
			var $results = $(document.createElement("ul"));

			var timeout = false;		// hold timeout ID for suggestion results to appear	
			var prevLength = 0;			// last recorded length of $input.val()
			var cache = [];				// cache MRU list
			var cacheSize = 0;			// size of cache in chars (bytes?)
			
			$results.addClass(options.resultsClass).appendTo('body');
				

			resetPosition();
			$(window)
				.load(resetPosition)		// just in case user is changing size of page while loading
				.resize(resetPosition);

			//$input.blur(function() {
			//	setTimeout(function() { $results.hide() }, 200);
			//});
			
			
			// help IE users if possible
			try {
				$results.bgiframe();
			} catch(e) { }


			// I really hate browser detection, but I don't see any other way
			if ($.browser.mozilla)
				$input.keypress(processKey);	// onkeypress repeats arrow keys in Mozilla/Opera
			else
				$input.keydown(processKey);		// onkeydown repeats arrow keys in IE/Safari
			
			
			var mouseOverResults = false;
			
			$results.mouseover(function(){
				mouseOverResults = true;
			});
			$results.bind("mouseenter",function(){
				mouseOverResults = true;
			});
			$results.bind("mouseleave",function(){
				mouseOverResults = false;
			});
			$input.blur(function(){
				if(!mouseOverResults) {
					$results.hide();
				}
			});

			function resetPosition() {
				// requires jquery.dimension plugin
				var offset = $input.offset();
				$results.css({
					top: (offset.top + input.offsetHeight) + 'px',
					left: offset.left + 'px'
				});
			}
			
			
			function processKey(e) {
				
				// handling up/down/escape requires results to be visible
				// handling enter/tab requires that AND a result to be selected
				if ((/27$|38$|40$/.test(e.keyCode) && $results.is(':visible')) ||
					(/^13$|^9$/.test(e.keyCode) && getCurrentResult())) {
		            
		            if (e.preventDefault)
		                e.preventDefault();
					if (e.stopPropagation)
		                e.stopPropagation();

					e.cancelBubble = true;
					e.returnValue = false;
				
					switch(e.keyCode) {
	
						case 38: // up
							prevResult();
							break;
				
						case 40: // down
							nextResult();
							break;
	
						case 9:  // tab
							mouseOverResults = false;
							break;
							
						case 13: // return
							selectCurrentResult();
							break;
							
						case 27: //	escape
							mouseOverResults = false;
							$results.hide();
							break;
	
					}
					
				} else if ($input.val().length != prevLength && e.keyCode != 9) {

					if (timeout) 
						clearTimeout(timeout);
					timeout = setTimeout(suggest, options.delay);
					prevLength = $input.val().length;
					
				}			
					
				
			}
			
			
			function suggest() {
			
				var q = $.trim($input.val());
				var lang = '';
				lang = $('body').attr('lang');

				if (q.length >= options.minchars) {
					
					$results.html('<li>loading..<span style="text-decoration: blink;">.</span></li>').show();
					
					cached = checkCache(q);
					
					if (cached) {
					
						displayItems(cached['items']);
						
					} else {

						var vocab = $(options.vocab).val().toLowerCase().replace('_', '-');
						vocab = vocab.substring(vocab.lastIndexOf("/"));
						
						var source = options.source;
						var data = "format=json&lang="+lang+"&q="+encodeURIComponent(q)
						if (source.indexOf('?') >= 0)
						{
							data = source.substring(source.indexOf('?') + 1) + '&' + data;
							source = source.substring(0, source.indexOf('?'));
						}

						$.ajax({
							processData: false,
							type: "GET",
							dataType: "json",
							url: (options.vocab == null ? source : source.replace('\$1', vocab)),
							data: data,
							success: function(result) {
									$results.hide();
									var items = parseJSON(result, q);
									displayItems(items);
									addToCache(q, items, result.length);
								}
						});	
					}
					
				} else {
				
					$results.hide();
					
				}
					
			}
			
			
			function checkCache(q) {

				for (var i = 0; i < cache.length; i++)
					if (cache[i]['q'] == q) {
						cache.unshift(cache.splice(i, 1)[0]);
						return cache[0];
					}
				
				return false;
			
			}
			
			function addToCache(q, items, size) {

				while (cache.length && (cacheSize + size > options.maxCacheSize)) {
					var cached = cache.pop();
					cacheSize -= cached['size'];
				}
				
				cache.push({
					q: q,
					size: size,
					items: items
					});
					
				cacheSize += size;
			
			}
			
			function displayItems(items) {
				
				if (!items)
					return;
					
				if (!items.length) {
					$results.hide();
					return;
				}
				
				var html = '';
				for (var i = 0; i < items.length; i++)
					html += '<li>' + items[i][0] + '</li>';

				resetPosition();
				$results.html(html).show();
				
				$results
					.children('li')
					.mouseover(function() {
						$results.children('li').removeClass(options.selectClass);
						$(this).addClass(options.selectClass);
					})
					.click(function(e) {
						e.preventDefault(); 
						e.stopPropagation();
						selectCurrentResult();
					})
					;
							
			}
			
			function parseJSON(jsonObject, q) {
				var items = [];

				var queryParts = q.split(' ');

				var expression = "";
				for(var i = 0; i < queryParts.length; i++) {
					if(queryParts[i] != ""){
						expression =  expression + queryParts[i] + "|";
					}
				}

				for (var i = 0; i < jsonObject.length; i++) {
					jsonObject[i].value = jsonObject[i].value.replace(
							new RegExp(expression, 'ig'), 
							function(q) { return '<span class="' + options.matchClass + '">' + q + '</span>' }
							);

					items[items.length] = new Array(jsonObject[i].value, jsonObject[i]);
				}

				return items;
			}
			
			function parseTxt(txt, q) {
				
				var items = [];
				var tokens = txt.split(options.delimiter);
				
				var queryParts = q.split(' ');
				
				var expression = "";
				for(var i = 0; i < queryParts.length; i++) {
					if(queryParts[i] != ""){
						expression =  expression + queryParts[i] + "|";
					}
				}
				
				// parse returned data for non-empty items
				for (var i = 0; i < tokens.length; i++) {
					
					var token = tokens[i].split(options.subdelimiter);
					token[0]= $.trim(token[0]);
					
				/*	var token = $.trim(tokens[i]);*/
					if (token[0]) {
						token[0] = token[0].replace(
							new RegExp(expression, 'ig'), 
							function(q) { return '<span class="' + options.matchClass + '">' + q + '</span>' }
							);
						items[items.length] = token;
					}
				}
				
				return items;
			}
			
			function getCurrentResult() {
			
				if (!$results.is(':visible'))
					return false;
			
				var $currentResult = $results.children('li.' + options.selectClass);
				
				if (!$currentResult.length)
					$currentResult = false;
					
				return $currentResult;

			}
			
			function getResult(result) {
				var id = 'test';
				var span = '<span class="' + options.matchClass + '">';
				for (var j = 0; j < cache.length; j++) {
					for(var i = 0; i < cache[j]['items'].length; i++) {
						var item = cache[j]['items'][i];
						item[0] = item[0].replace(new RegExp(span,'ig'),'');
						item[0] = item[0].replace(new RegExp('</span>','ig'),'');
						item[0] = $.trim(item[0]);
						//alert(item[0] + '==' + result + " : " + (item[0]==result));
						if (item[0] == result) {
							obj =item[1];
							break;
						}
					}
				}
				return obj;
			}
			
			function selectCurrentResult() {
			
				$currentResult = getCurrentResult();
				
				
			
				if ($currentResult) {
					$input.val($currentResult.text());
					var resultObj = getResult($currentResult.text());
					$input.resultID = resultObj['id'];
					$input.resultLanguage = resultObj['language'];
					$input.resultValue = $currentResult.text();
					$input.result = resultObj;
					$results.hide();
					options.onSelect.apply($input);
				}
			
			}
			
			function nextResult() {
			
				$currentResult = getCurrentResult();
			
				if ($currentResult)
					$currentResult
						.removeClass(options.selectClass)
						.next()
							.addClass(options.selectClass);
				else
					$results.children('li:first-child').addClass(options.selectClass);
			
			}
			
			function prevResult() {
			
				$currentResult = getCurrentResult();
			
				if ($currentResult)
					$currentResult
						.removeClass(options.selectClass)
						.prev()
							.addClass(options.selectClass);
				else
					$results.children('li:last-child').addClass(options.selectClass);
			
			}
	
		}
		
		$.fn.suggest = function(source, options) {
		
			if (!source)
				return;

			
			options = options || {};
			options.source = source;
			options.delay = options.delay || 100;
			options.resultsClass = options.resultsClass || 'ac_results';
			options.selectClass = options.selectClass || 'ac_over';
			options.matchClass = options.matchClass || 'ac_match';
			options.minchars = options.minchars || 2;
			options.delimiter = options.delimiter || '\n';
			options.subdelimiter = options.subdelimiter || '|';
			options.onSelect = options.onSelect || false;
			options.maxCacheSize = options.maxCacheSize || 65536;
			//options.vocab = options.vocab;
	
			this.each(function() {
				new $.suggest(this, options);
			});
	
			return this;
			
		};
		
	})(jQuery);
	
