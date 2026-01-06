const user = JSON.parse(localStorage.getItem('user'))?? null

export const getCurentUserId = () => {
    return user.id;
};

export const getCurrentUserInfos = () => {
    return user.user_metadata;
};