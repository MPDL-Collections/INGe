package de.mpg.mpdl.inge.inge_validation.validator;

import java.util.List;

import com.baidu.unbiz.fluentvalidator.ValidationError;
import com.baidu.unbiz.fluentvalidator.Validator;
import com.baidu.unbiz.fluentvalidator.ValidatorContext;
import com.baidu.unbiz.fluentvalidator.ValidatorHandler;

import de.mpg.mpdl.inge.inge_validation.util.ErrorMessages;
import de.mpg.mpdl.inge.inge_validation.util.ValidationTools;
import de.mpg.mpdl.inge.model.db.valueobjects.FileDbVO;
import de.mpg.mpdl.inge.model.valueobjects.FileVO;

/*
 * <!-- Entered dates have to be in the format YYYY, YYYY-MM or YYYY-MM-DD --> <iso:pattern
 * name="correct_date_format" id="correct_date_format"> <iso:rule context="dcterms:available">
 * <iso:assert
 * test=". = '' or (matches(., '^\d\d\d\d(-\d\d){0,2}$') and substring(concat(.,'-01-01'), 1, 10) castable as xs:date)"
 * > DateFormatIncorrect </iso:assert> </iso:rule>
 * 
 * <iso:rule context="dcterms:dateCopyrighted"> <iso:assert
 * test=". = '' or (matches(.,'^\d\d\d\d(-\d\d){0,2}$') and substring(concat(., '-01-01'), 1, 10) castable as xs:date)"
 * > DateFormatIncorrect </iso:assert> </iso:rule> </iso:pattern>
 */

public class ComponentsDateFormatValidator extends ValidatorHandler<List<FileDbVO>> implements Validator<List<FileDbVO>> {

  @Override
  public boolean validate(ValidatorContext context, List<FileDbVO> files) {

    boolean ok = true;

    if (ValidationTools.isNotEmpty(files)) {

      for (final FileDbVO fileVO : files) {

        if (fileVO != null && fileVO.getMetadata() != null) {

          if (!ValidationTools.checkDate(fileVO.getMetadata().getCopyrightDate())) {
            context.addError(ValidationError.create(ErrorMessages.DATE_FORMAT_INCORRECT).setField("copyrightDate"));
            ok = false;
          }

          if (!ValidationTools.checkDate(fileVO.getMetadata().getEmbargoUntil())) {
            context.addError(ValidationError.create(ErrorMessages.DATE_FORMAT_INCORRECT).setField("available"));
            ok = false;
          }

        } // if

      } // for

    } // if

    return ok;
  }

}
