/*
 *
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License, Version 1.0 only
 * (the "License"). You may not use this file except in compliance
 * with the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE
 * or http://www.escidoc.de/license.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at license/ESCIDOC.LICENSE.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 */
/*
 * Copyright 2006-2011 Fachinformationszentrum Karlsruhe Gesellschaft
 * für wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Förderung der Wissenschaft e.V.
 * All rights reserved. Use is subject to license terms.
 */
function addItemListFunctions(){
    $('.itemList').each(function(i, ele){
        $(ele).find('.mediumView').each(function(j, elem){
            $(elem).hide();
        });
        $(ele).find('.collapse').each(function(j, elem){
            $(elem).hide();
        });
        $(ele).find('.expand').each(function(j, elem){
            $(elem).show();
        });
        $(ele).find('.collapseTriangle').each(function(j, elem){
            $(elem).hide();
        });
        $(ele).find('.expandTriangle').each(function(j, elem){
            $(elem).show();
        });
        $(ele).find('.listItem').hover(function(){
            $(this).addClass('listBackground');
        }, function(){
            $(this).removeClass('listBackground');
        });
    });
    
    $('html').click(function(){
        $('.selectMenu').hide();
    });
    
    $('.selectMenu').click(function(e){
        e.stopPropagation();
    });
    
    $('.checkboxSelectButton').click(function(){
        $(this).siblings('.selectMenu').toggle(100);
    });
    var tog = false;
    $('.listHeader').find('.allCheckBox').click(function(){
        $('.itemList').find("input[type=checkbox]").attr("checked", !tog);
        tog = !tog;
    });
    $('.selectMenu').find('.selectTotal').click(function(){
        $(this).parents('.itemList').find('.itemCheckBox').each(function(i, elem){
            elem.checked = true;
        });
        $(this).parents('.selectMenu').hide();
    });
    $('.selectMenu').find('.selectAll').click(function(){
        $(this).parents('.itemList').find('.itemCheckBox').each(function(i, elem){
            elem.checked = true;
        });
        $(this).parents('.selectMenu').hide();
    });
	    $('.selectMenu').find('.toggleAll').click(function(){
        $('.itemHeader').toggleCheckboxes();
        $(this).parents('.selectMenu').hide();
    });
    $('.selectMenu').find('.selectNone').click(function(){
        $(this).parents('.itemList').find('.itemCheckBox').each(function(i, elem){
            elem.checked = false;
        });
    });
    $('.selectMenu').find('a').each(function(i, elem){
        $(elem).click(function(){
            $(this).parents('.selectMenu').hide()
        });
    });
    
    $('.headerSwitchView').find('.expand').click(function(){
        $(this).hide();
        $(this).siblings('.collapse').show();
        $(this).parents('.itemList').find('.listItem').find('.expand:visible').each(function(i, elem){
            $(elem).trigger('click');
        });
    });
    $('.headerSwitchView').find('.expandTriangle').click(function(){
        $(this).hide();
        $(this).siblings('.collapseTriangle').show();
        $(this).parents('.itemList').find('.listItem').find('.expandTriangle:visible').each(function(i, elem){
            $(elem).trigger('click');
        });
    });
    $('.headerSwitchView').find('.collapse').click(function(){
        $(this).hide();
        $(this).siblings('.expand').show();
        $(this).parents('.itemList').find('.listItem').find('.collapse:visible').each(function(i, elem){
            $(elem).trigger('click');
        });
    });
    
    $('.headerSwitchView').find('.collapseTriangle').click(function(){
        $(this).hide();
        $(this).siblings('.expandTriangle').show();
        $(this).parents('.itemList').find('.listItem').find('.collapseTriangle:visible').each(function(i, elem){
            $(elem).trigger('click');
        });
    });
    
    $('.shortView').find('.expand').each(function(i, ele){
        $(ele).click(function(){
            $(this).hide();
            $(this).siblings('.collapse').show();
            var parentElement = $(this).parents('.listItem');
            $(parentElement).children('.mediumView').slideToggle('normal', function(){
                if (($(parentElement).find('.shortView').find('.expand:visible').length) ==
                0) {
                    $(parentElement).find('.headerSwitchView').find('.expand').hide();
                    $(parentElement).find('.headerSwitchView').find('.collapse').show();
                }
            });
        })
    });
    
    $('.shortView').find('.expandTriangle').each(function(i, ele){
        $(ele).click(function(){
            $(this).hide();
            $(this).siblings('.collapseTriangle').show();
            var parentElement = $(this).parents('.listItem');
            $(parentElement).children('.mediumView').slideToggle('normal', function(){
                if (($(parentElement).find('.itemHeader').find('.expandTriangle:visible').length) ==
                0) {
                    $(parentElement).find('.headerSwitchView').find('.expandTriangle').hide();
                    $(parentElement).find('.headerSwitchView').find('.collapseTriangle').show();
                }
            });
        })
    });
    
    $('.shortView').find('.collapse').each(function(i, ele){
        $(ele).click(function(){
            $(this).hide();
            $(this).siblings('.expand').show();
            var parentElement = $(this).parents('.listItem');
            $(parentElement).children('.mediumView').slideToggle('normal', function(){
                if (($(parentElement).find('.shortView').find('.collapse:visible').length) ==
                0) {
                    $(parentElement).find('.headerSwitchView').find('.expand').show();
                    $(parentElement).find('.headerSwitchView').find('.collapse').hide();
                }
            });
        })
    });
    
    $('.shortView').find('.collapseTriangle').each(function(i, ele){
        $(ele).click(function(){
            $(this).hide();
            $(this).siblings('.expandTriangle').show();
            var parentElement = $(this).parents('.listItem');
            $(parentElement).children('.mediumView').slideToggle('normal', function(){
                if (($(parentElement).find('.itemHeader').find('.collapseTriangle:visible').length) ==
                0) {
                    $(parentElement).find('.headerSwitchView').find('.expandTriangle').show();
                    $(parentElement).find('.headerSwitchView').find('.collapseTriangle').hide();
                }
            });
        })
    });
    
}

function installItemList(){
    /* ADD LISTENERS TO CHANGED DOM */
    addItemListFunctions();
}
