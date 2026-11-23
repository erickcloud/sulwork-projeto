/** @vitest-environment jsdom */

import '@testing-library/jest-dom/vitest';
import {afterEach, describe, expect, it, vi} from 'vitest';
import {cleanup, render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import App from './App';

afterEach(() => {
    cleanup();
    vi.unstubAllGlobals();
});

describe('Sulwork frontend', () => {
    it('renderiza a tela de eventos corretamente', async () => {
        vi.stubGlobal(
            'fetch',
            vi.fn().mockResolvedValue({
                ok: true,
                json: async () => [],
            })
        );

        render(<App/>);

        expect(
            screen.getByRole('heading', {name: 'Novo evento'})
        ).toBeInTheDocument();

        expect(
            screen.getByRole('textbox', {name: 'Nome do evento'})
        ).toBeInTheDocument();

        expect(
            screen.getByRole('button', {name: 'Criar evento'})
        ).toBeInTheDocument();

        expect(
            await screen.findByText('Nenhum evento cadastrado ainda.')
        ).toBeInTheDocument();
    });

    it('permite preencher o nome e selecionar o tipo do evento', async () => {
        vi.stubGlobal(
            'fetch',
            vi.fn().mockResolvedValue({
                ok: true,
                json: async () => [],
            })
        );

        const user = userEvent.setup();

        render(<App/>);

        const campoNome = screen.getByRole('textbox', {
            name: 'Nome do evento',
        });

        const campoTipo = screen.getByRole('combobox', {
            name: 'Tipo do evento',
        });

        await user.type(campoNome, 'Almoço de confraternização');
        await user.selectOptions(campoTipo, 'JANTAR');

        expect(campoNome).toHaveValue('Almoço de confraternização');
        expect(campoTipo).toHaveValue('JANTAR');
    });

    it('envia os dados corretos ao cadastrar um evento', async () => {
        const fetchMock = vi.fn()
            .mockResolvedValueOnce({
                ok: true,
                json: async () => [],
            })
            .mockResolvedValueOnce({
                ok: true,
            })
            .mockResolvedValueOnce({
                ok: true,
                json: async () => [],
            });

        vi.stubGlobal('fetch', fetchMock);

        const user = userEvent.setup();

        render(<App/>);

        const campoNome = screen.getByRole('textbox', {
            name: 'Nome do evento',
        });

        const campoTipo = screen.getByRole('combobox', {
            name: 'Tipo do evento',
        });

        await user.type(campoNome, 'Evento de teste');
        await user.selectOptions(campoTipo, 'JANTAR');

        await user.click(
            screen.getByRole('button', {name: 'Criar evento'})
        );

        expect(
            await screen.findByText('Evento criado com sucesso.')
        ).toBeInTheDocument();

        expect(campoNome).toHaveValue('');

        expect(fetchMock).toHaveBeenNthCalledWith(
            2,
            'http://localhost:8080/api/eventos',
            expect.objectContaining({
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: expect.stringMatching(
                    /"nome":"Evento de teste".*"tipo":"JANTAR"/
                ),
            })
        );
    });

    it('exibe mensagem quando a API rejeita o cadastro', async () => {
        const fetchMock = vi.fn()
            .mockResolvedValueOnce({
                ok: true,
                json: async () => [],
            })
            .mockResolvedValueOnce({
                ok: false,
            });

        vi.stubGlobal('fetch', fetchMock);

        const user = userEvent.setup();

        render(<App/>);

        await user.type(
            screen.getByRole('textbox', {name: 'Nome do evento'}),
            'Evento com erro'
        );

        await user.click(
            screen.getByRole('button', {name: 'Criar evento'})
        );

        expect(
            await screen.findByText(
                'Não foi possível criar o evento. Confira a API.'
            )
        ).toBeInTheDocument();
    });


    it('exibe mensagem quando não consegue conectar à API ao cadastrar', async () => {
        const fetchMock = vi.fn()
            .mockResolvedValueOnce({
                ok: true,
                json: async () => [],
            })
            .mockRejectedValueOnce(new TypeError('Failed to fetch'));

        vi.stubGlobal('fetch', fetchMock);

        const user = userEvent.setup();

        render(<App />);

        await user.type(
            screen.getByRole('textbox', { name: 'Nome do evento' }),
            'Evento sem conexão'
        );

        await user.click(
            screen.getByRole('button', { name: 'Criar evento' })
        );

        expect(
            await screen.findByText(
                'Não foi possível conectar à API. Verifique se o Docker Compose está ativo.'
            )
        ).toBeInTheDocument();
    });


});
