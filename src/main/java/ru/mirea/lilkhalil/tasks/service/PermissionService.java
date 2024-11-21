package ru.mirea.lilkhalil.tasks.service;

public interface PermissionService<T> {
    boolean isPermitted(T object);
}
