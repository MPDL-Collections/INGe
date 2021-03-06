package de.mpg.mpdl.inge.inge_validation.validator;

import java.util.List;

import com.baidu.unbiz.fluentvalidator.ValidationError;
import com.baidu.unbiz.fluentvalidator.Validator;
import com.baidu.unbiz.fluentvalidator.ValidatorContext;
import com.baidu.unbiz.fluentvalidator.ValidatorHandler;

import de.mpg.mpdl.inge.inge_validation.util.ErrorMessages;
import de.mpg.mpdl.inge.inge_validation.util.ValidationTools;
import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.OrganizationVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.PersonVO;

/*
 * <!-- if any fields at "Source creator" are filled, "role" has to be filled also. --> <iso:pattern
 * name="source_creator_role_required" id="source_creator_role_required"> <iso:rule
 * context="source:source/escidoc:creator"> <iso:assert test="@role != '' or
 * not(person:person/escidoc:family-name != '' or person:person/escidoc:given-name != '' or
 * organization:organization/dc:title != '' or person:person/organization:organization/dc:title !=
 * '' or organization:organization/escidoc:address != '' or
 * person:person/organization:organization/escidoc:address != '')">
 * CreatorRoleNotProvided</iso:assert> </iso:rule> </iso:pattern>
 */

public class CreatorsRoleRequiredValidator extends ValidatorHandler<List<CreatorVO>> implements Validator<List<CreatorVO>> {

  @Override
  public boolean validate(ValidatorContext context, List<CreatorVO> creators) {

    boolean ok = true;

    if (ValidationTools.isNotEmpty(creators)) {

      int i = 1;
      for (final CreatorVO creatorVO : creators) {

        if (creatorVO != null && creatorVO.getRole() == null) {

          switch (creatorVO.getType()) {

            case ORGANIZATION:

              final OrganizationVO o = creatorVO.getOrganization();
              if (o != null) {
                if (ValidationTools.isNotEmpty(o.getName()) //
                    || ValidationTools.isNotEmpty(o.getAddress())) {
                  context.addError(ValidationError.create(ErrorMessages.CREATOR_ROLE_NOT_PROVIDED).setField("creator[" + i + "]"));
                  ok = false;
                }
              }

              break;

            case PERSON:

              final PersonVO p = creatorVO.getPerson();
              if (p != null) {
                if (ValidationTools.isNotEmpty(p.getFamilyName()) //
                    || ValidationTools.isNotEmpty(p.getGivenName())) {
                  context.addError(ValidationError.create(ErrorMessages.CREATOR_ROLE_NOT_PROVIDED).setField("creator[" + i + "]"));
                  ok = false;

                  break;
                }
              }

              final List<OrganizationVO> orgs = p.getOrganizations();

              if (ValidationTools.isNotEmpty(orgs)) {

                int j = 1;
                for (final OrganizationVO organizationVO : orgs) {

                  if (organizationVO != null) {
                    if (ValidationTools.isNotEmpty(organizationVO.getName()) //
                        || ValidationTools.isNotEmpty(organizationVO.getAddress())) {
                      context.addError(ValidationError.create(ErrorMessages.CREATOR_ROLE_NOT_PROVIDED) //
                          .setField("creator[" + i + "].organization[" + j + "]"));
                      ok = false;

                      break;
                    }

                  }

                  j++;
                } // for

              } // if

              break;

          } // switch

        } // if

        i++;
      } // for

    } // if

    return ok;
  }

}
