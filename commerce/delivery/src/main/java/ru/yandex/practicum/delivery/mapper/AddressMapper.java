package ru.yandex.practicum.delivery.mapper;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;
import ru.yandex.practicum.delivery.model.Address;
import ru.yandex.practicum.dto.warehouse.AddressDto;

@UtilityClass
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddressMapper {
    public Address toAddress(AddressDto addressDto) {
        return Address.builder()
            .country(addressDto.getCountry())
            .city(addressDto.getCity())
            .street(addressDto.getStreet())
            .house(addressDto.getHouse())
            .flat(addressDto.getFlat())
            .build();
    }

    public AddressDto toAddressDto(Address address) {
        return AddressDto.builder()
            .country(address.getCountry())
            .city(address.getCity())
            .street(address.getStreet())
            .house(address.getHouse())
            .flat(address.getFlat())
            .build();
    }
}
