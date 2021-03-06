package de.mpg.mpdl.inge.inge_validation.validator;

import com.baidu.unbiz.fluentvalidator.Validator;
import com.baidu.unbiz.fluentvalidator.ValidatorContext;
import com.baidu.unbiz.fluentvalidator.ValidatorHandler;

import de.mpg.mpdl.inge.inge_validation.util.ErrorMessages;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO.Genre;

/*
 * <!-- Genre is required --> <iso:pattern name="genre_required" id="genre_required"> <iso:rule
 * context="publication:publication"> <iso:assert test="@type != ''"> GenreNotProvided</iso:assert>
 * </iso:rule> </iso:pattern>
 */

public class GenreRequiredValidator extends ValidatorHandler<Genre> implements Validator<Genre> {

  @Override
  public boolean validate(ValidatorContext context, Genre genre) {

    if (genre == null) {
      context.addErrorMsg(ErrorMessages.GENRE_NOT_PROVIDED);
      return false;
    }

    return true;
  }

}
