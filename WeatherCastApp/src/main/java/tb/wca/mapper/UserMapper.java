package tb.wca.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tb.wca.dto.UserCreateResponseDTO;
import tb.wca.entity.UserEntity;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserCreateResponseDTO entityToResponseDto(UserEntity entity);
}
