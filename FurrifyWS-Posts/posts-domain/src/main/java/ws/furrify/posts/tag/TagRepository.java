package ws.furrify.posts.tag;

interface TagRepository {
    Tag save(Tag tag);

    boolean existsByValue(String value);
}
