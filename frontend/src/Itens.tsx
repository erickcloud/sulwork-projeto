
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

type Item = {
    id: number;
    nome: string;
    categoria: {
        id: number;
        nome: string;
    };
    quantidadeNecessaria: number;
    obrigatorio: boolean;
    alergenos: string;
};

type Props = {
    eventos: Evento[];
};

const API = 'http://localhost:8080/api';

export default function Itens({eventos}: Props) {
    const [eventoId, setEventoId] = useState('');
    const [categoriaId, setCategoriaId] = useState('');
    const [categorias, setCategorias] = useState<Categoria[]>([]);
    const [itens, setItens] = useState<Item[]>([]);
    const [nome, setNome] = useState('');
    const [quantidade, setQuantidade] = useState('1');
    const [obrigatorio, setObrigatorio] = useState(true);
    const [alergenos, setAlergenos] = useState('');
    const [mensagem, setMensagem] = useState('');

    useEffect(() => {
        if (eventos.length > 0 && !eventoId) {
            setEventoId(String(eventos[0].id));
        }
    }, [eventos, eventoId]);

    async function carregarCategorias() {
        if (!eventoId) {
            setCategorias([]);
            setCategoriaId('');
            return;
        }

        try {
            const resposta = await fetch(
                `${API}/categorias/evento/${eventoId}`
            );

            if (!resposta.ok) {
                setMensagem('Não foi possível carregar as categorias.');
                return;
            }

            const dados: Categoria[] = await resposta.json();
            setCategorias(dados);

            setCategoriaId(atual =>
                dados.some(categoria => String(categoria.id) === atual)
                    ? atual
                    : dados.length > 0 ? String(dados[0].id) : ''
            );
        } catch {
            setMensagem('Erro de conexão com a API.');
        }
    }

    async function carregarItens() {
        if (!categoriaId) {
            setItens([]);
            return;
        }

        try {
            const resposta = await fetch(
                `${API}/itens/categoria/${categoriaId}`
            );

            if (!resposta.ok) {
                setMensagem('Não foi possível carregar os itens.');
                return;
            }

            setItens(await resposta.json());
            setMensagem('');
        } catch {
            setMensagem('Erro de conexão com a API.');
        }
    }

    useEffect(() => {
        carregarCategorias();
    }, [eventoId]);

    useEffect(() => {
        carregarItens();
    }, [categoriaId]);

    async function cadastrar(e: React.FormEvent) {
        e.preventDefault();

        if (!categoriaId) {
            setMensagem('Cadastre ou selecione uma categoria primeiro.');
            return;
        }

        try {
            const resposta = await fetch(`${API}/itens`, {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify({
                    categoriaId: Number(categoriaId),
                    nome,
                    quantidadeNecessaria: Number(quantidade),
                    obrigatorio,
                    alergenos
                })
            });

            if (!resposta.ok) {
                setMensagem('Não foi possível cadastrar o item. Confira os dados.');
                return;
            }

            setNome('');
            setQuantidade('1');
            setObrigatorio(true);
            setAlergenos('');
            setMensagem('Item cadastrado com sucesso.');

            await carregarItens();
        } catch {
            setMensagem('Erro de conexão com a API.');
        }
    }

    return (
        <section className="panel">
            <h2>Itens do evento</h2>

            {eventos.length === 0 ? (
                <p className="muted">Cadastre um evento primeiro.</p>
            ) : (
                <>
                    <label htmlFor="evento-item">Evento</label>
                    <select
                        id="evento-item"
                        aria-label="Evento dos itens"
                        value={eventoId}
                        onChange={e => setEventoId(e.target.value)}
                    >
                        {eventos.map(evento => (
                            <option key={evento.id} value={evento.id}>
                                {evento.nome}
                            </option>
                        ))}
                    </select>

                    {categorias.length === 0 ? (
                        <p className="muted">
                            Cadastre uma categoria para este evento antes de adicionar itens.
                        </p>
                    ) : (
                        <>
                            <label htmlFor="categoria-item">Categoria</label>
                            <select
                                id="categoria-item"
                                aria-label="Categoria do item"
                                value={categoriaId}
                                onChange={e => setCategoriaId(e.target.value)}
                            >
                                {categorias.map(categoria => (
                                    <option key={categoria.id} value={categoria.id}>
                                        {categoria.nome}
                                    </option>
                                ))}
                            </select>

                            <form onSubmit={cadastrar}>
                                <input
                                    aria-label="Nome do item"
                                    placeholder="Nome do alimento ou produto"
                                    value={nome}
                                    onChange={e => setNome(e.target.value)}
                                    required
                                />

                                <input
                                    aria-label="Quantidade necessária"
                                    type="number"
                                    min="0.01"
                                    step="0.01"
                                    value={quantidade}
                                    onChange={e => setQuantidade(e.target.value)}
                                    required
                                />

                                <label>
                                    <input
                                        type="checkbox"
                                        checked={obrigatorio}
                                        onChange={e => setObrigatorio(e.target.checked)}
                                    />
                                    Item obrigatório
                                </label>

                                <input
                                    aria-label="Alérgenos"
                                    placeholder="Alérgenos (ex.: glúten, leite, amendoim)"
                                    value={alergenos}
                                    onChange={e => setAlergenos(e.target.value)}
                                />

                                <button type="submit">Cadastrar item</button>
                            </form>

                            {mensagem && <p role="status">{mensagem}</p>}

                            <h3>Itens cadastrados</h3>

                            {itens.length === 0 ? (
                                <p className="muted">Nenhum item cadastrado nesta categoria.</p>
                            ) : (
                                itens.map(item => (
                                    <div className="event" key={item.id}>
                                        <div>
                                            <strong>{item.nome}</strong>
                                            <small>
                                                Quantidade necessária: {item.quantidadeNecessaria}
                                            </small>
                                            <small>
                                                {item.obrigatorio ? 'Obrigatório' : 'Opcional'}
                                            </small>
                                            <small>
                                                Alérgenos: {item.alergenos || 'Não informado'}
                                            </small>
                                        </div>
                                    </div>
                                ))
                            )}

                            <button
                                type="button"
                                className="secondary"
                                onClick={carregarItens}
                            >
                                Atualizar itens
                            </button>
                        </>
                    )}

                    {mensagem && <p role="status">{mensagem}</p>}
                </>
            )}
        </section>
    );
}
