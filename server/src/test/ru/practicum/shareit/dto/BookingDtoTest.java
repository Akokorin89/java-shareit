package ru.practicum.shareit.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;


@JsonTest
public class BookingDtoTest {
    @Autowired
    JacksonTester<BookingDto> json;

    @Test
    void testBookingDto() throws Exception {

        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));
        bookingDto.setStatus(BookingStatus.APPROVED);
        bookingDto.setItem(new BookingDto.Item(2L, "test"));

        JsonContent<BookingDto> result = json.write(bookingDto);

        assertThat(result).hasJsonPathStringValue("$.start");
        assertThat(result).hasJsonPathStringValue("$.end");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo("test");
    }
}
