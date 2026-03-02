const parseErrorMessage = async (response, fallbackMessage) => {
  try {
    const contentType = response.headers.get("content-type") || "";
    if (contentType.includes("application/json")) {
      const data = await response.json();
      if (data?.message) {
        return data.message;
      }
    } else {
      const text = await response.text();
      if (text) {
        return text;
      }
    }
  } catch (_) {
    // Ignore parsing errors and fallback to generic message.
  }
  return fallbackMessage;
};

const buildQueryString = (params) => {
  const search = new URLSearchParams();
  Object.entries(params).forEach(([key, value]) => {
    if (value !== undefined && value !== null && value !== "") {
      search.set(key, String(value));
    }
  });
  return search.toString();
};

const get = async (url, token) => {
  const response = await fetch(url, {
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${token}`
    }
  });

  if (!response.ok) {
    const message = await parseErrorMessage(response, "Request failed.");
    const error = new Error(message);
    error.status = response.status;
    throw error;
  }

  return response.json();
};

const del = async (url, token) => {
  const response = await fetch(url, {
    method: "DELETE",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${token}`
    }
  });

  if (!response.ok) {
    const message = await parseErrorMessage(response, "Delete failed.");
    const error = new Error(message);
    error.status = response.status;
    throw error;
  }
};

export const adminApi = {
  listUsers: ({ token, page, size, sort, q }) => {
    const query = buildQueryString({ page, size, sort, q });
    return get(`/api/admin/users?${query}`, token);
  },

  listGames: ({ token, page, size, sort, q }) => {
    const query = buildQueryString({ page, size, sort, q });
    return get(`/api/admin/games?${query}`, token);
  },

  deleteUser: ({ token, id }) => del(`/api/admin/users/${encodeURIComponent(id)}`, token),
  deleteGame: ({ token, id }) => del(`/api/admin/games/${encodeURIComponent(id)}`, token)
};
