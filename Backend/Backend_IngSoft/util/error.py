from typing import TypeVar, Type, Callable, Any, cast

T = TypeVar("T")
E = TypeVar("E")


def raises(exc_type: Type[E]) -> Callable[[Callable[[Any], T]], Callable[[Any], T]]:
    def decorator(func: Callable[[Any], T]) -> Callable[[Any], T]:
        def wrapper(*args: Any, **kwargs: Any) -> T:
            try:
                return func(*args, **kwargs)
            except exc_type as e:
                raise e

        return cast(Callable[[Any], T], wrapper)

    return decorator
