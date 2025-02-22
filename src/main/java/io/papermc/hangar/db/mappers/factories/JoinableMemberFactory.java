package io.papermc.hangar.db.mappers.factories;

import io.papermc.hangar.model.db.UserTable;
import io.papermc.hangar.model.db.roles.ExtendedRoleTable;
import io.papermc.hangar.model.internal.user.JoinableMember;
import org.jdbi.v3.core.config.ConfigRegistry;
import org.jdbi.v3.core.generic.GenericTypes;
import org.jdbi.v3.core.mapper.NoSuchMapperException;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.mapper.RowMapperFactory;
import org.jdbi.v3.core.mapper.RowMappers;

import java.lang.reflect.Type;
import java.util.Optional;

public class JoinableMemberFactory implements RowMapperFactory {

    @SuppressWarnings("unchecked")
    @Override
    public Optional<RowMapper<?>> build(Type type, ConfigRegistry config) {
        if (!JoinableMember.class.equals(GenericTypes.getErasedType(type))) {
            return Optional.empty();
        }

        Type tableType = GenericTypes.resolveType(JoinableMember.class.getTypeParameters()[0], type);
        if (!ExtendedRoleTable.class.isAssignableFrom(GenericTypes.getErasedType(tableType))) {
            return Optional.empty();
        }
        Class<? extends ExtendedRoleTable<?, ?>> extendedRoleTableType = (Class<? extends ExtendedRoleTable<?, ?>>) tableType;

        RowMappers rowMappers = config.get(RowMappers.class);
        RowMapper<? extends ExtendedRoleTable<?, ?>> tableMapper = rowMappers.findFor(extendedRoleTableType).orElseThrow(() -> new NoSuchMapperException("Could not find mapper for " + tableType.getTypeName()));
        RowMapper<UserTable> userTableMapper = rowMappers.findFor(UserTable.class).orElseThrow(() -> new NoSuchMapperException("Could not find mapper for " + UserTable.class.getTypeName()));



        RowMapper<JoinableMember<?>> mapper = (rs, ctx) -> new JoinableMember<>(tableMapper.map(rs, ctx), userTableMapper.map(rs, ctx));
        return Optional.of(mapper);
    }
}
