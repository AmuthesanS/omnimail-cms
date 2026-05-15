import { FormEvent, useEffect, useState } from "react";
import { Template, createTemplate, listTemplates, listVersions } from "./api";

export default function App() {
  const [templates, setTemplates] = useState<Template[]>([]);
  const [name, setName] = useState("");
  const [mjml, setMjml] = useState("<html><body><h1>Hello {{firstName}}</h1></body></html>");
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  async function refresh() {
    setLoading(true);
    setError(null);
    try {
      setTemplates(await listTemplates());
    } catch (e) {
      setError(e instanceof Error ? e.message : "Failed to load");
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    refresh();
  }, []);

  async function onSubmit(e: FormEvent) {
    e.preventDefault();
    setError(null);
    try {
      await createTemplate(name, mjml);
      setName("");
      await refresh();
    } catch (err) {
      setError(err instanceof Error ? err.message : "Create failed");
    }
  }

  return (
    <div className="layout">
      <header>
        <h1>OmniMail CMS</h1>
        <p>Push templates to the database and retrieve versions.</p>
      </header>

      {error && <p className="error">{error}</p>}

      <section className="card">
        <h2>New template (draft)</h2>
        <form onSubmit={onSubmit}>
          <label>
            Name
            <input value={name} onChange={(e) => setName(e.target.value)} required />
          </label>
          <label>
            MJML / HTML source
            <textarea value={mjml} onChange={(e) => setMjml(e.target.value)} rows={6} />
          </label>
          <button type="submit">Push to DB</button>
        </form>
      </section>

      <section className="card">
        <h2>Templates {loading && "(loading…)"}</h2>
        <ul className="list">
          {templates.map((t) => (
            <TemplateRow key={t.id} template={t} />
          ))}
        </ul>
        {!loading && templates.length === 0 && <p>No templates yet.</p>}
      </section>
    </div>
  );
}

function TemplateRow({ template }: { template: Template }) {
  const [versions, setVersions] = useState<{ version: number; status: string }[]>([]);

  useEffect(() => {
    listVersions(template.id)
      .then((v) => setVersions(v.map((x) => ({ version: x.version, status: x.status }))))
      .catch(() => setVersions([]));
  }, [template.id]);

  return (
    <li>
      <strong>{template.name}</strong> <span className="muted">({template.id.slice(0, 8)}…)</span>
      <ul>
        {versions.map((v) => (
          <li key={v.version}>
            v{v.version} — {v.status}
          </li>
        ))}
      </ul>
    </li>
  );
}
