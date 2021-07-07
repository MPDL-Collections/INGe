﻿﻿(function ($) {
    $.fn.extend({
        smartpaginator: function (options) {
            var settings = $.extend({
                totalrecords: 0,
                recordsperpage: 0,
                length: 10,
                next: 'Next',
                prev: 'Prev',
                first: ' ',
                last: ' ',
                go: 'Go',
                theme: 'green',
                display: 'double',
                initval: 1,
                datacontainer: '', //data container id
                dataelement: '', //children elements to be filtered e.g. tr or div
                onchange: null,
                controlsalways: false,
                paginatorInfo : null //id of an element to which the paginator information ("1 - 10 of 597") is displayed
            }, options);
            return this.each(function () {
                var currentPage = 0;
                var startPage = 0;
                var totalpages = parseInt(settings.totalrecords / settings.recordsperpage);
                if (settings.totalrecords % settings.recordsperpage > 0) totalpages++;
                var initialized = false;
                var container = $(this).addClass('pageBrowser').addClass(settings.theme);
                container.find('a').remove();
                container.find('div').remove();
                container.find('span').remove();
                var dataContainer;
                var dataElements;
                if (settings.datacontainer != '') {
                    dataContainer = $('#' + settings.datacontainer);
                    dataElements = $('' + settings.dataelement + '', dataContainer);
                }
                //var list = $('<ul/>');
                var btnPrev = $('<a class="prevBtn"/>').text(settings.prev).click(function () {
                	if ($(this).hasClass('disabled')) return false; 
                	currentPage = parseInt(container.find('a.paginatorNumber.actual').text()) - 1; 
                	navigate(--currentPage); 
                	}).addClass('backward');
                var btnNext = $('<a class="nextBtn"/>').text(settings.next).click(function () {
                	if ($(this).hasClass('disabled')) return false; 
                	currentPage = parseInt(container.find('a.paginatorNumber.actual').text()); 
                	navigate(currentPage); 
                	}).addClass('forward');
                var btnFirst = $('<a class="firstBtn" style="max-height:1em;"/>').text(settings.first).click(function () {
                	if ($(this).hasClass('disabled')) return false;
                	currentPage = 0; 
                	navigate(0); 
                	}).addClass('min_imgBtn skipToFirst');
                var btnLast = $('<a class="lastBtn"  style="max-height:1em;"/>').text(settings.last).click(function () {
                	if ($(this).hasClass('disabled')) return false;
                	currentPage = totalpages - 1; navigate(currentPage); 
                	}).addClass('min_imgBtn skipToLast');
                var inputPage = $('<input/>').attr('type', 'text').keydown(function (e) {
                    if (isTextSelected(inputPage)) inputPage.val('');
                    if (e.which >= 48 && e.which < 58) {
                        var value = parseInt(inputPage.val() + (e.which - 48));
                        if (!(value > 0 && value <= totalpages)) e.preventDefault();
                    } else if (!(e.which == 8 || e.which == 46)) e.preventDefault();
                });
                var btnGo = $('<input/>').attr('type', 'button').attr('value', settings.go).addClass('btn').click(function () { if (inputPage.val() == '') return false; else { currentPage = parseInt(inputPage.val()) - 1; navigate(currentPage); } });
                container.append(btnFirst).append(btnPrev).append(btnNext).append(btnLast);//.append($('<div/>').addClass('short').append(inputPage).append(btnGo));
                if (settings.display == 'single') {
                    btnGo.css('display', 'none');
                    inputPage.css('display', 'none');
                }
                buildNavigation(startPage);
                if (settings.initval == 0) settings.initval = 1;
                currentPage = settings.initval - 1;
                navigate(currentPage);
                initialized = true;
                function showLabels(pageIndex) {
                    if(settings.paginatorInfo) {
                    	var textContainer = $('#' + settings.paginatorInfo);
                    	if(textContainer.length) {
                    		
                    		textContainer.empty();
                            var upper = (pageIndex + 1) * settings.recordsperpage;
                            if (upper > settings.totalrecords) upper = settings.totalrecords;
                            textContainer.text("(" + (pageIndex * settings.recordsperpage + 1) + " - " + upper + " of " + settings.totalrecords + ")");
                    		
                    	}
                    	
                    }
                	
                	
                }
                function buildNavigation(startPage) {
                    container.find('a.paginatorNumber, span.seperator, span.separator').remove();
                    if (settings.totalrecords <= settings.recordsperpage) return;
                    for (var i = startPage; i < startPage + settings.length; i++) {
                        if (i == totalpages) break;
                        container.find('a.nextBtn').before($('<a class="paginatorNumber xSmall_txtBtn"/>').addClass('actual')
                                    .attr('id', (i + 1)).addClass(settings.theme)
                                    .attr('href', 'javascript:void(0)')
                                    .text(i + 1)
                                    .click(function () {
                                        currentPage = startPage + $(this).closest('a.paginatorNumber').prevAll('a.paginatorNumber').length;
                                        navigate(currentPage);
                                    }));
                        var css = {
                        		"background-color":"#000000",
                        		"width":1,
                        		"height":"1.3636em"
                        		
                        }
                        container.find('a.nextBtn').before($('<span class="separator"/>').css(css));
                       
                    }
                    container.find('a.prevBtn').after($('<span class="separator"/>').css(css));
                    showLabels(startPage);
                    inputPage.val((startPage + 1));
                    container.find('a.paginatorNumber').addClass(settings.theme).removeClass('actual');
                    container.find('a.paginatorNumber:eq(0)').addClass(settings.theme).addClass('actual');
                    //set width of paginator
                    var sW = container.find('a.paginatorNumber:eq(0) a').outerWidth() + (parseInt(container.find('a.paginatorNumber:eq(0)').css('margin-left')) * 2);
                    var width = sW * container.find('a.paginatorNumber').length;
                    //container.css({ width: width });
                    showRequiredButtons(startPage);
                }
                function navigate(topage) {
                    //make sure the page in between min and max page count
                    var index = topage;
                    var mid = settings.length / 2;
                    if (settings.length % 2 > 0) mid = (settings.length + 1) / 2;
                    var startIndex = 0;
                    if (topage >= 0 && topage < totalpages) {
                        if (topage >= mid) {
                            if (totalpages - topage > mid)
                                startIndex = topage - (mid - 1);
                            else if (totalpages > settings.length)
                                startIndex = totalpages - settings.length;
                        }
                        buildNavigation(startIndex); 
                        showLabels(currentPage);
                        container.find('a.paginatorNumber').removeClass('actual');
                        inputPage.val(currentPage + 1);
                        container.find('a.paginatorNumber[id="' + (index + 1) + '"]').addClass('actual');
                        var recordStartIndex = currentPage * settings.recordsperpage;
                        var recordsEndIndex = recordStartIndex + settings.recordsperpage;
                        if (recordsEndIndex > settings.totalrecords)
                            recordsEndIndex = settings.totalrecords % recordsEndIndex;
                        if (initialized) {
                            if (settings.onchange != null) {
                                settings.onchange((currentPage + 1), recordStartIndex, recordsEndIndex);
                            }
                        }
                        if (dataContainer != null) {
                            if (dataContainer.length > 0) {
                                //hide all elements first
                                dataElements.css('display', 'none');
                                //display elements that need to be displayed
                                if ($(dataElements[0]).find('th').length > 0) { //if there is a header, keep it visible always
                                    $(dataElements[0]).css('display', '');
                                    recordStartIndex++;
                                    recordsEndIndex++;
                                }
                                for (var i = recordStartIndex; i < recordsEndIndex; i++)
                                    $(dataElements[i]).css('display', '');
                            }
                        }

                        showRequiredButtons();
                    }
                }
                function showRequiredButtons() {
                   // if (totalpages > settings.length) {
                        if (currentPage > 0) {
                            if (!settings.controlsalways) {
                                btnPrev.css('display', '');
                            }
                            else {
                                btnPrev.css('display', '').removeClass('disabled');
                            }
                        }
                        else {
                            if (!settings.controlsalways) {
                                btnPrev.css('display', 'none');
                            }
                            else {
                                btnPrev.css('display', '').addClass('disabled');
                            }
                        }
                        if (currentPage > 0) {
                            if (!settings.controlsalways) {
                                btnFirst.css('display', '');
                            }
                            else {
                                btnFirst.css('display', '').removeClass('disabled');
                            }
                        }
                        else {
                            if (!settings.controlsalways) {
                                btnFirst.css('display', 'none');
                            }
                            else {
                                btnFirst.css('display', '').addClass('disabled');
                            }
                        }

                        if (currentPage == totalpages - 1) {
                            if (!settings.controlsalways) {
                                btnNext.css('display', 'none');
                            }
                            else {
                                btnNext.css('display', '').addClass('disabled');
                            }
                        }
                        else {
                            if (!settings.controlsalways) {
                                btnNext.css('display', '');
                            }
                            else {
                                btnNext.css('display', '').removeClass('disabled');
                            }
                        }
                        if (totalpages - 1 > currentPage) {
                            if (!settings.controlsalways) {
                                btnLast.css('display', '');
                            }
                            else {
                                btnLast.css('display', '').removeClass('disabled');
                            }
                        }
                        else {
                            if (!settings.controlsalways) {
                                btnLast.css('display', 'none');
                            }
                            else {
                                btnLast.css('display', '').addClass('disabled');
                            }
                        };
                    //}
                    /*
                    else {
                        if (!settings.controlsalways) {
                            btnFirst.css('display', 'none');
                            btnPrev.css('display', 'none');
                            btnNext.css('display', 'none');
                            btnLast.css('display', 'none');
                        }
                        else {
                            btnFirst.css('display', '').addClass('disabled');
                            btnPrev.css('display', '').addClass('disabled');
                            btnNext.css('display', '').addClass('disabled');
                            btnLast.css('display', '').addClass('disabled');
                        }
                   }
                   */
                }
                function isTextSelected(el) {
                    var startPos = el.get(0).selectionStart;
                    var endPos = el.get(0).selectionEnd;
                    var doc = document.selection;
                    if (doc && doc.createRange().text.length != 0) {
                        return true;
                    } else if (!doc && el.val().substring(startPos, endPos).length != 0) {
                        return true;
                    }
                    return false;
                }
            });
        }
    });
})(jQuery);