package ru.practicum.shareit.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class ItemDtoTest {

    @Autowired
    JacksonTester<ItemDto> json;

    @Test
    void testItemDto() throws Exception {

        ItemDto itemFoundDto = new ItemDto();
        itemFoundDto.setId(1L);
        itemFoundDto.setName("test");
        itemFoundDto.setDescription("test description");
        itemFoundDto.setAvailable(true);

        JsonContent<ItemDto> result = json.write(itemFoundDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("test");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("test description");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
    }
}
