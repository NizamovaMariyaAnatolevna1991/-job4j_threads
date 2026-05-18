package ru.job4j.cash;

import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;

class CacheTest {

    @Test
    public void whenAddFind() throws OptimisticException {
        var base = new Base(1,  "Base", 1);
        var cache = new Cache();
        cache.add(base);
        var find = cache.findById(base.id());
        assertThat(find.get().name())
                .isEqualTo("Base");
    }

    @Test
    public void whenAddUpdateFind() throws OptimisticException {
        var base = new Base(1, "Base", 1);
        var cache = new Cache();
        cache.add(base);
        cache.update(new Base(1, "Base updated", 1));
        var find = cache.findById(base.id());
        assertThat(find.get().name())
                .isEqualTo("Base updated");
    }

    @Test
    public void whenAddDeleteFind() throws OptimisticException {
        var base = new Base(1,   "Base", 1);
        var cache = new Cache();
        cache.add(base);
        cache.delete(1);
        var find = cache.findById(base.id());
        assertThat(find.isEmpty()).isTrue();
    }

    @Test
    public void whenMultiUpdateThrowException() throws OptimisticException {
        var base = new Base(1,  "Base", 1);
        var cache = new Cache();
        cache.add(base);
        cache.update(base);
        assertThatThrownBy(() -> cache.update(base))
                .isInstanceOf(OptimisticException.class);
    }

    @Test
    public void whenAddDuplicateReturnsFalse() throws OptimisticException {
        var cache = new Cache();
        var base = new Base(1, "Base", 1);
        cache.add(base);
        assertThat(cache.add(new Base(1, "Duplicate", 1))).isFalse();
    }

    @Test
    public void whenUpdateNonExistentThrowsNoSuchElementException() {
        var cache = new Cache();
        assertThatThrownBy(() -> cache.update(new Base(99, "New", 1)))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    public void whenUpdateWithWrongVersionThrowsOptimisticException() throws OptimisticException {
        var cache = new Cache();
        var base = new Base(1, "Base", 1);
        cache.add(base);
        assertThatThrownBy(() -> cache.update(new Base(1, "Updated", 99)))
                .isInstanceOf(OptimisticException.class);
    }

    @Test
    public void whenUpdateSuccessfullyIncrementsVersion() throws OptimisticException {
        var cache = new Cache();
        cache.add(new Base(1, "Base", 1));
        cache.update(new Base(1, "Updated", 1));

        var found = cache.findById(1);
        assertThat(found).isPresent();
        assertThat(found.get().name()).isEqualTo("Updated");
        assertThat(found.get().version()).isEqualTo(2);
    }

    @Test
    public void whenFindByIdNonExistentReturnsEmpty() {
        var cache = new Cache();
        assertThat(cache.findById(99)).isEmpty();
    }
}