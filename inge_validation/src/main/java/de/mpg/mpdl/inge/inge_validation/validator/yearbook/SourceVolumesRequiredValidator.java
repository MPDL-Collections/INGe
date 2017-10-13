package de.mpg.mpdl.inge.inge_validation.validator.yearbook;

import java.util.List;

import com.baidu.unbiz.fluentvalidator.ValidationError;
import com.baidu.unbiz.fluentvalidator.Validator;
import com.baidu.unbiz.fluentvalidator.ValidatorContext;
import com.baidu.unbiz.fluentvalidator.ValidatorHandler;

import de.mpg.mpdl.inge.inge_validation.util.ErrorMessages;
import de.mpg.mpdl.inge.inge_validation.util.ValidationTools;
import de.mpg.mpdl.inge.model.valueobjects.metadata.SourceVO;

/*
 * <iso:report test="not(escidoc:volume != '')">SourceVolumeNotProvided</iso:report>
 */

public class SourceVolumesRequiredValidator extends ValidatorHandler<List<SourceVO>> implements
    Validator<List<SourceVO>> {

  @Override
  public boolean validate(ValidatorContext context, List<SourceVO> sources) {

    boolean ok = true;

    if (ValidationTools.isNotEmpty(sources)) {

      int i = 1;
      for (final SourceVO sourceVO : sources) {

        if (sourceVO != null) {
          if (sourceVO.getTitle() == null) {
            context.addError(ValidationError.create(ErrorMessages.SOURCE_VOLUME_NOT_PROVIDED)
                .setField("source[" + i + "]"));
            ok = false;
          }
        }

        i++;
      } // for

    } // if

    return ok;
  }

}
