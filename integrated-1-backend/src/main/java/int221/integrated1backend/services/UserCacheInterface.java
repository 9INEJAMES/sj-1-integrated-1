package int221.integrated1backend.services;

import int221.integrated1backend.entities.in.UserCache;

public interface UserCacheInterface {
    UserCache save(UserCache userCache);
    UserCache delete(UserCache userCache);
}
