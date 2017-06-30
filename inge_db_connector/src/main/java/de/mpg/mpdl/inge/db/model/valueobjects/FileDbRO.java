package de.mpg.mpdl.inge.db.model.valueobjects;

import javax.persistence.MappedSuperclass;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(value = Include.NON_EMPTY)
@MappedSuperclass
public class FileDbRO extends BasicDbRO {

}
