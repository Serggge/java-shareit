package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;
import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {

	private long itemId;
	@FutureOrPresent
	private LocalDateTime start;
	@Future
	private LocalDateTime end;

}
