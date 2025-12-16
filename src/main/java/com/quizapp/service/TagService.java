package com.quizapp.service;

import com.quizapp.entity.Tag;
import com.quizapp.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TagService {

    @Autowired
    private TagRepository tagRepository;

    public Tag createOrGetTag(String tagName) {
        Optional<Tag> existingTag = tagRepository.findByName(tagName.trim());
        return existingTag.orElseGet(() -> tagRepository.save(new Tag(tagName.trim())));
    }

    public List<Tag> createOrGetTags(List<String> tagNames) {
        List<Tag> tags = new ArrayList<>();
        for (String tagName : tagNames) {
            if (tagName != null && !tagName.trim().isEmpty()) {
                tags.add(createOrGetTag(tagName.trim()));
            }
        }
        return tags;
    }

    public List<Tag> getAllTags() {
        return tagRepository.findAllOrderByName();
    }

    public List<Tag> searchTags(String searchTerm) {
        return tagRepository.findByNameContainingIgnoreCase(searchTerm);
    }

    public void deleteTag(Long id) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tag not found"));
        tagRepository.delete(tag);
    }

    public Optional<Tag> getTagById(Long id) {
        return tagRepository.findById(id);
    }
}