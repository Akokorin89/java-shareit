package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;

    @Override
    public ItemRequest create(ItemRequest itemRequest) {
        if (itemRequest.getDescription() == null || itemRequest.getDescription().isBlank()) {
            throw new ValidateException("Description is empty");
        }
        itemRequest.setCreated(LocalDateTime.now().withNano(0));
        return itemRequestRepository.save(itemRequest);
    }

    @Override
    public List<ItemRequest> findByRequesterId(long requesterId) {
        //Check user
        userRepository.findById(requesterId).orElseThrow(() -> new NoSuchElementException("User not found"));
        return itemRequestRepository.findByRequester_IdOrderByCreatedAsc(requesterId);
    }

    @Override
    public List<ItemRequest> findAll(long userId, int from, int size) {
        return itemRequestRepository.findByRequester_IdNot(
                userId,
                PageRequest.of(from / size, size, Sort.by("created").descending())
        );
    }

    @Override
    public ItemRequest findById(long id) {
        return itemRequestRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Request not found"));
    }
}
