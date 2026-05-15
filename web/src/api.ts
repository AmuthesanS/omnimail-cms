const API_BASE = import.meta.env.VITE_API_URL ?? "";

const headers: Record<string, string> = {
  "Content-Type": "application/json",
  "X-Tenant-Id": "default",
  "X-User-Id": "dev-user",
  "X-User-Roles": "editor,reviewer,publisher,admin",
};

async function request<T>(path: string, options?: RequestInit): Promise<T> {
  const res = await fetch(`${API_BASE}${path}`, { ...options, headers: { ...headers, ...options?.headers } });
  if (!res.ok) {
    const body = await res.json().catch(() => ({}));
    throw new Error((body as { message?: string }).message ?? res.statusText);
  }
  return res.json() as Promise<T>;
}

export interface Template {
  id: string;
  tenantId: string;
  name: string;
  tags: string[];
}

export interface TemplateVersion {
  templateVersionId: string;
  templateId: string;
  version: number;
  status: string;
  mjmlSource?: string;
}

export function listTemplates(): Promise<Template[]> {
  return request("/api/v1/templates");
}

export function createTemplate(name: string, mjmlSource: string): Promise<Template> {
  return request("/api/v1/templates", {
    method: "POST",
    body: JSON.stringify({ name, tags: [], mjmlSource, variablesSchema: {} }),
  });
}

export function listVersions(templateId: string): Promise<TemplateVersion[]> {
  return request(`/api/v1/templates/${templateId}/versions`);
}
