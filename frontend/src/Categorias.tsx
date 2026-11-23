
import {useEffect, useState} from 'react';

type Evento = {
    id: number;
    nome: string;
};

type Categoria = {
    id: number;
    nome: string;
    meta: number | null;
};

type Props = {
    eventos: Evento[];
};

const API = 'http://localhost:8080/api';

export default function Categorias({eventos}: Props) {
    const [eventoId, setEventoId] = useState('');
    const [categorias, setCategorias] = useState<Categoria[]>([]);
    const [nome, setNome] = useState('');
    const [meta, setMeta] = useState('');
    const [mensagem, setMensagem] = useState('');

    useEffect(() => {
        if (eventos.length > 0 && !eventoId) {
            setEventoId(String(eventos[0].id));
        }
    }, [eventos, eventoId]);

    async function carregar() {
        if (!eventoId) return;

        try {
            const resposta = await fetch(
                `${API}/categorias/evento/${eventoId}`
            );

            if (!resposta.ok) {
                setMensagem('Não foi possível carregar as categorias.');
                return;
            }

            setCategorias(await resposta.json());
            setMensagem('');
        } catch {
            setMensagem('Erro de conexão com a API.');
        }
    }

    useEffect(() => {
        carregar();
    }, [eventoId]);

    async function cadastrar(e: React.FormEvent) {
        e.preventDefault();

        if (!eventoId) {
            setMensagem('Cadastre ou selecione um evento primeiro.');
            return;
        }

        try {
            const resposta = await fetch(`${API}/categorias`, {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify({
                    eventoId: Number(eventoId),
                    nome,
                    meta: meta ? Number(meta) : null
                })
            });

            if (!resposta.ok) {
                setMensagem('Não foi possível cadastrar a categoria.');
                return;
            }

            setNome('');
            setMeta('');
            setMensagem('Categoria cadastrada com sucesso.');
            await carregar();
        } catch {
            setMensagem('Erro de conexão com a API.');
        }
    }

    return (
        <section className="panel">
            <h2>Categorias</h2>

            {eventos.length === 0 ? (
                <p className="muted">Cadastre um evento antes de criar categorias.</p>
            ) : (
                <>
                    <label htmlFor="evento-categoria">Evento</label>
                    <select
                        id="evento-categoria"
                        aria-label="Evento da categoria"
                        value={eventoId}
                        onChange={e => setEventoId(e.target.value)}
                    >
                        {eventos.map(evento => (
                            <option key={evento.id} value={evento.id}>
                                {evento.nome}
                            </option>
                        ))}
                    </select>

                    <form onSubmit={cadastrar}>
                        <input
                            aria-label="Nome da categoria"
                            placeholder="Nome da categoria"
                            value={nome}
                            onChange={e => setNome(e.target.value)}
                            required
                        />

                        <input
                            aria-label="Meta da categoria"
                            type="number"
                            min="0"
                            step="0.01"
                            placeholder="Meta (opcional)"
                            value={meta}
                            onChange={e => setMeta(e.target.value)}
                        />

                        <button type="submit">Cadastrar categoria</button>
                    </form>

                    {mensagem && <p role="status">{mensagem}</p>}

                    <h3>Categorias do evento</h3>

                    {categorias.length === 0 ? (
                        <p className="muted">Nenhuma categoria cadastrada.</p>
                    ) : (
                        categorias.map(categoria => (
                            <div className="event" key={categoria.id}>
                                <div>
                                    <strong>{categoria.nome}</strong>
                                    <small>
                                        Meta: {categoria.meta ?? 'Não definida'}
                                    </small>
                                </div>
                            </div>
                        ))
                    )}

                    <button
                        type="button"
                        className="secondary"
                        onClick={carregar}
                    >
                        Atualizar categorias
                    </button>
                </>
            )}
        </section>
    );
}
