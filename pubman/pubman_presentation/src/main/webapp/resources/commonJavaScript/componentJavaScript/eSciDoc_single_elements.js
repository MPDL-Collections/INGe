/*QUICK SEARCH INITIALISATION*/

function addQuickSearchFunction(){
	$pb('.quickSearchTextInput').keyup(function(keyEvent){
		if(keyEvent.keyCode == '13'){
			$pb(this).parents('.searchMenu').find('.quickSearchBtn').click();
		};
	});
};


/*DATE INPUT FIELD*/

function validateDate(inputField) {
	/*DATE VALIDATION ACCORDING TO THE GREGORIAN CALENDAR*/
	var input_empty = "";
	var isValidDate = true;
	var isBC = false;
	var validChars = "0123456789-";
	var validNumbers = "0123456789";
	var bcString = "BC";
	var possibleDate = inputField.value;
	if((inputField.value=="")||(inputField.value==input_empty)) {
		$pb(inputField).val(input_empty).addClass("blankInput");
	}
	if(!(inputField.value == input_empty)) {
		/*REMOVE SPACES*/
		while(inputField.value.match(' -')) inputField.value = inputField.value.replace(/ -/, '-');
		while(inputField.value.match('- ')) inputField.value = inputField.value.replace(/- /, '-');
		/*REMOVE LEADING SPACES*/
		while(inputField.value.indexOf(' ')==0) inputField.value = inputField.value.substring(1,inputField.value.length);
		/*REMOVE SPACES AT THE END*/
		while(inputField.value.lastIndexOf(' ')==inputField.value.length-1) inputField.value = inputField.value.substring(0,inputField.value.length-1);
		while(inputField.value.match(' '+bcString)) inputField.value = inputField.value.replace(/ BC/, bcString);
		inputField.value = inputField.value.replace(bcString, ' '+bcString);
		possibleDate = inputField.value;
		/*CHECK FOR BC*/
		if( (possibleDate.indexOf(bcString))== (possibleDate.length-2)  ) {
			possibleDate = possibleDate.replace(' '+bcString, '');
			isBC= true;
		}
		/*VALIDATE DATE*/
		for (j = 0; j < possibleDate.length && isValidDate == true; j++) {
			Char = possibleDate.charAt(j); 
    		if (!(validChars.indexOf(Char) == -1)) 
    	     {
    	     	var subType = possibleDate.split(/-/);
    	     	if((subType.length < 4) && (subType.length > 0) && (possibleDate.lastIndexOf('-')<possibleDate.length-1) && (possibleDate.indexOf('-')!=0) ) {
    	     		for(var k=0; k < subType.length; k++) {
    	     			switch(k) {
    	     				/*FIRST NUMBER HAS NOT FOUR DIGITS*/
    	     				case 0:	if(subType[k].length!=4) isValidDate = false;
    	     						break;
    	     				/*SECOND NUMBER HAS NOT TWO DIGITS AND/OR IS LESS THAN 1 OR BIGGER THAN 12*/
    	     				case 1: if((subType[k].length!=2) || (subType[k]>12) || (subType[k]<1)) isValidDate = false;
        	 						break;
        	 				/*THIRD NUMBER HAS NOT TWO DIGITS AND/OR IS LESS THAN 1 OR BIGGER THAN 31*/
        	 				case 2: if((subType[k].length!=2) || (subType[k]>31) || (subType[k]<1)) isValidDate = false;
        	 						else {
        	 								/*APRIL, JUNE, SEPTEMBER AND NOVEMBER HAVE MORE THAN 30 DAYS*/
        	 								if(((subType[k-1]=='04') || (subType[k-1]=='06') || (subType[k-1]=='09') || (subType[k-1]=='11')) && (subType[k]>30)) isValidDate = false;
        	 								/*
        	 								*FEBRUARY HAS MORE THAN 28 DAYS IN REGULAR YEARS (YEAR mod 4 is bigger than 0 OR year mod 100=0 AND year mod 400 is bigger than 0)
        	 								*FEBRUARY HAS MORE THAN 29 DAYS IN LEAP YEARS (all others)
        	 								*/
        	 								if((subType[k-1]=='02') && ( ((subType[k]>29) && ((subType[k-2]%4) == 0)) || (   ((subType[k]>28) && ((subType[k-2]%4)>0)) || (((subType[k-2]%100)==0) && ((subType[k-2]%400)>0) && (subType[k]>28)  )   )  ) ) isValidDate = false;
        	  							}
        	 						break;
        	 			}
        	 		}
        	 	}
        	 	else isValidDate = false;
       		}
       		else isValidDate = false;
		}

		if(!(isValidDate)) {
			$pb(inputField).addClass("falseValue");
		} else $pb(inputField).removeClass("falseValue");
	}
}

function addDateJSLabels() {
	/*
	*This function adds the following HTML code
	*
	*<div class="dateJSBox *LENGTH VALUE HERE*_area0">
	*	GIVEN INPUT FIELD HERE
	*	<label class="dateJSLabel *LENGTH VALUE HERE*_negMarginLIncl noDisplay" for="*INPUT FIELD ID*"></label>
	*</div>
	*
	*/
	$pb(".dateJSInput").each(function(){
		var classNameString = $pb(this).attr("class");
		var lengthValue;
		var possibleLengthValues = classNameString.split(' ');
		for(var i=0; i<possibleLengthValues.length; i++) {
			if(possibleLengthValues[i].match('_txtInput')) {
				var wholeLengthValue = possibleLengthValues[i].split('_');
				lengthValue = wholeLengthValue[0];
			}
		}
		$pb(this).wrap('<div class="dateJSBox '+lengthValue+'_area0"></div>');
		$pb(this).after('<label class="dateJSLabel '+lengthValue+'_label '+lengthValue+'_negMarginLIncl noDisplay" for="'+$pb(this).attr("id")+'"></label>');
	});
}

function dateParse(dateString)
{
	dateString = dateString.replace(/(\d\d\d\d)(-\d\d)?(-\d\d)?/, '$1$2$3');
	return Date.parse(dateString);
}

// Deactivated due to datejs bug, see http://jira.mpdl.mpg.de/browse/PUBMAN-1719
//function addDateJSFunctions() {}
function addDateJSFunctions() {
	$pb(".dateJSInput").each(function(){
		$pb(this).focus(function() {
			var input_empty = "", empty_string = "";
			
			$pb(this).removeClass("falseValue");
			
			if($pb(this).val() === input_empty)
			{
				$pb(this).val(empty_string);
				$pb(this).removeClass("blankInput");
			}
	
			if($pb(this).val() != "")
			{
				var date = null;
				date = dateParse($pb(this).val());

				if(date!=null)
				{
					$pb(".dateJSLabel[for='"+$pb(this).attr("id")+"']").removeClass("noDisplay").text(date.toString("yyyy-MMMM-dd"));
				}
			}
	        return false;    
		});
		$pb(this).blur(function(){
			var input_empty = "", empty_string = "";
		
			$pb(".dateJSLabel[for='"+$pb(this).attr("id")+"']").addClass("noDisplay").text("");
			
			if($pb(this).val() === empty_string)
			{
				$pb(this).val(input_empty).addClass("blankInput");
			}
			validateDate(this);
		});
		$pb(this).keyup(function(event){
			var message = "";
			var input_empty = "", empty_string = "";
			var date = null;

			$pb(".dateJSLabel[for='"+$pb(this).attr("id")+"']").text("");
			if($pb(this).val() != "")
			{
				date = dateParse($pb(this).val());
				
				if(date != null)
				{
					$pb(".dateJSLabel[for='"+$pb(this).attr("id")+"']").removeClass("noDisplay").text(date.toString("yyyy-MMMM-dd"));
					var oEvent = event || window.event;
					if(oEvent.keyCode == 13)
					{
						$pb(this).val(date.toString("yyyy-MM-dd"));
						$pb(".dateJSLabel[for='"+$pb(this).attr("id")+"']").addClass("noDisplay").text("");
					};
				} else
					{
						$pb(".dateJSLabel[for='"+$pb(this).attr("id")+"']").addClass("noDisplay").text(message);
					}
			}
			else
				{
					$pb(".dateJSLabel[for='"+$pb(this).attr("id")+"']").addClass("noDisplay").text("");
				}			
	      	var evt = event || window.event;
	      	if(evt.stopPropagation) evt.stopPropagation();
		 	evt.cancelBubble = true;
		});
		validateDate(this);
	});
}

function installQuickSearchShortcut() {
	addQuickSearchFunction();
}

function installDateTextbox() {
	/*GET LANGUAGE*/
	var language = '';
	language = document.body.lang;
	if(language != '') language = '-'+language;
	/*INCLUDE RIGHT LANGUAGE HERE*/
	include_dom(jsURL + 'externalJavaScript/DateJS/date'+language+'.js');
	addDateJSLabels();
	addDateJSFunctions();
}

function installSameHeight() {
	$pb('.sameHeightSlave').each(function(i,elem){$pb(elem).height($pb('.sameHeightMaster').height());});
}