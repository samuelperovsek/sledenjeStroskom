import { useEffect, useState } from 'react'

const API = 'http://localhost:8080/api'

const prazenObrazec = {
    title: '',
    amount: '',
    kategorijaId: '',
    date: '',
    note: '',
}

function App() {
    const [stroski, setStroski] = useState([])
    const [kategorije, setKategorije] = useState([])
    const [statistika, setStatistika] = useState([])
    const [obrazec, setObrazec] = useState(prazenObrazec)
    const [urejaId, setUrejaId] = useState(null)
    const [izbran, setIzbran] = useState(null)
    const [napaka, setNapaka] = useState('')

    const [iskanje, setIskanje] = useState('')
    const [filterKategorija, setFilterKategorija] = useState('')
    const [datumOd, setDatumOd] = useState('')
    const [datumDo, setDatumDo] = useState('')

    const naloziStroske = () => {
        const params = new URLSearchParams()
        if (iskanje) params.append('q', iskanje)
        if (filterKategorija) params.append('kategorijaId', filterKategorija)
        if (datumOd) params.append('datumOd', datumOd)
        if (datumDo) params.append('datumDo', datumDo)

        fetch(`${API}/stroski/iskanje?${params}`)
            .then((res) => res.json())
            .then((data) => setStroski(data))
    }

    const naloziStatistiko = () => {
        fetch(`${API}/stroski/statistika`)
            .then((res) => res.json())
            .then((data) => setStatistika(data))
    }

    useEffect(() => {
        fetch(`${API}/kategorije`)
            .then((res) => res.json())
            .then((data) => setKategorije(data))
        naloziStatistiko()
    }, [])

    useEffect(() => {
        naloziStroske()
    }, [iskanje, filterKategorija, datumOd, datumDo])

    const pocistiFiltre = () => {
        setIskanje('')
        setFilterKategorija('')
        setDatumOd('')
        setDatumDo('')
    }

    const spremeniPolje = (e) => {
        setObrazec({ ...obrazec, [e.target.name]: e.target.value })
    }

    const ponastavi = () => {
        setObrazec(prazenObrazec)
        setUrejaId(null)
    }

    const shrani = (e) => {
        e.preventDefault()
        setNapaka('')

        const telo = {
            title: obrazec.title,
            amount: Number(obrazec.amount),
            kategorija: { id: Number(obrazec.kategorijaId) },
            date: obrazec.date,
            note: obrazec.note,
        }

        const url = urejaId ? `${API}/stroski/${urejaId}` : `${API}/stroski`
        const metoda = urejaId ? 'PUT' : 'POST'

        fetch(url, {
            method: metoda,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(telo),
        }).then((res) => {
            if (res.ok) {
                ponastavi()
                naloziStroske()
                naloziStatistiko()
            } else {
                setNapaka('Shranjevanje ni uspelo. Preveri podatke.')
            }
        })
    }

    const uredi = (s) => {
        setUrejaId(s.id)
        setObrazec({
            title: s.title,
            amount: s.amount,
            kategorijaId: s.kategorija ? s.kategorija.id : '',
            date: s.date,
            note: s.note || '',
        })
        setNapaka('')
    }

    const poglej = (id) => {
        setNapaka('')
        fetch(`${API}/stroski/${id}`).then((res) => {
            if (res.ok) {
                res.json().then((data) => setIzbran(data))
            } else {
                setNapaka('Zapisa ni mogoče prebrati.')
            }
        })
    }

    const izbrisi = (id) => {
        if (!window.confirm('Ali res želiš izbrisati strošek?')) return

        fetch(`${API}/stroski/${id}`, { method: 'DELETE' }).then((res) => {
            if (res.ok) {
                if (urejaId === id) ponastavi()
                if (izbran && izbran.id === id) setIzbran(null)
                naloziStroske()
                naloziStatistiko()
            } else {
                setNapaka('Brisanje ni uspelo.')
            }
        })
    }

    const skupajVse = statistika.reduce((vsota, s) => vsota + Number(s.skupaj), 0)

    return (
        <div style={{ maxWidth: 800, margin: '2rem auto', fontFamily: 'sans-serif' }}>
            <h1>Stroški</h1>

            <h2>{urejaId ? 'Urejanje stroška' : 'Nov strošek'}</h2>
            <form onSubmit={shrani} style={{ display: 'grid', gap: 8, marginBottom: 24 }}>
                <input
                    name="title"
                    placeholder="Naziv"
                    value={obrazec.title}
                    onChange={spremeniPolje}
                    required
                />
                <input
                    name="amount"
                    type="number"
                    step="0.01"
                    min="0.01"
                    placeholder="Znesek"
                    value={obrazec.amount}
                    onChange={spremeniPolje}
                    required
                />
                <select
                    name="kategorijaId"
                    value={obrazec.kategorijaId}
                    onChange={spremeniPolje}
                    required
                >
                    <option value="">Izberi kategorijo</option>
                    {kategorije.map((k) => (
                        <option key={k.id} value={k.id}>
                            {k.naziv}
                        </option>
                    ))}
                </select>
                <input
                    name="date"
                    type="date"
                    value={obrazec.date}
                    onChange={spremeniPolje}
                    required
                />
                <input
                    name="note"
                    placeholder="Opomba"
                    value={obrazec.note}
                    onChange={spremeniPolje}
                />
                <button type="submit">{urejaId ? 'Shrani spremembe' : 'Dodaj'}</button>
                {urejaId && (
                    <button type="button" onClick={ponastavi}>
                        Prekliči
                    </button>
                )}
                {napaka && <p style={{ color: 'red' }}>{napaka}</p>}
            </form>

            <h2>Statistika po kategorijah</h2>
            <table border="1" cellPadding="6" style={{ width: '100%', marginBottom: 24 }}>
                <thead>
                <tr>
                    <th>Kategorija</th>
                    <th>Število stroškov</th>
                    <th>Skupaj</th>
                </tr>
                </thead>
                <tbody>
                {statistika.map((s) => (
                    <tr key={s.kategorija}>
                        <td>{s.kategorija}</td>
                        <td>{s.stevilo}</td>
                        <td>{Number(s.skupaj).toFixed(2)} €</td>
                    </tr>
                ))}
                <tr>
                    <td colSpan="2"><strong>Skupaj vse</strong></td>
                    <td><strong>{skupajVse.toFixed(2)} €</strong></td>
                </tr>
                </tbody>
            </table>

            <h2>Seznam</h2>
            <div style={{ display: 'flex', gap: 8, flexWrap: 'wrap', marginBottom: 12 }}>
                <input
                    placeholder="Išči po nazivu ali opombi"
                    value={iskanje}
                    onChange={(e) => setIskanje(e.target.value)}
                />
                <select
                    value={filterKategorija}
                    onChange={(e) => setFilterKategorija(e.target.value)}
                >
                    <option value="">Vse kategorije</option>
                    {kategorije.map((k) => (
                        <option key={k.id} value={k.id}>
                            {k.naziv}
                        </option>
                    ))}
                </select>
                <label>
                    Od: <input type="date" value={datumOd} onChange={(e) => setDatumOd(e.target.value)} />
                </label>
                <label>
                    Do: <input type="date" value={datumDo} onChange={(e) => setDatumDo(e.target.value)} />
                </label>
                <button onClick={pocistiFiltre}>Počisti</button>
            </div>

            <table border="1" cellPadding="6" style={{ width: '100%' }}>
                <thead>
                <tr>
                    <th>Naziv</th>
                    <th>Znesek</th>
                    <th>Kategorija</th>
                    <th>Datum</th>
                    <th>Opomba</th>
                    <th></th>
                </tr>
                </thead>
                <tbody>
                {stroski.map((s) => (
                    <tr key={s.id}>
                        <td>{s.title}</td>
                        <td>{s.amount} €</td>
                        <td>{s.kategorija?.naziv}</td>
                        <td>{s.date}</td>
                        <td>{s.note}</td>
                        <td>
                            <button onClick={() => poglej(s.id)}>Poglej</button>{' '}
                            <button onClick={() => uredi(s)}>Uredi</button>{' '}
                            <button onClick={() => izbrisi(s.id)}>Izbriši</button>
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>
            {stroski.length === 0 && <p>Ni zadetkov.</p>}

            {izbran && (
                <div style={{ border: '1px solid #888', padding: 12, marginTop: 24 }}>
                    <h2>Podrobnosti stroška</h2>
                    <p><strong>ID:</strong> {izbran.id}</p>
                    <p><strong>Naziv:</strong> {izbran.title}</p>
                    <p><strong>Znesek:</strong> {izbran.amount} €</p>
                    <p><strong>Kategorija:</strong> {izbran.kategorija?.naziv}</p>
                    <p><strong>Datum:</strong> {izbran.date}</p>
                    <p><strong>Opomba:</strong> {izbran.note}</p>
                    <button onClick={() => setIzbran(null)}>Zapri</button>
                </div>
            )}
        </div>
    )
}

export default App