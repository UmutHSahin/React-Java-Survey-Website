import { useState } from 'react'
import Login from "/Users/umuthasansahin/Desktop/anket/frontend/anketFe/src/components/LoginSignup.jsx";
import './App.css'
import Homepage from './components/HomePage';
import LoginSignup from '/Users/umuthasansahin/Desktop/anket/frontend/anketFe/src/components/LoginSignup.jsx';
import SurveyPage from './components/SurveyPage';
import ProfilePage from './components/ProfilePage';
import AdminPage from './components/AdminPage';

import { Routes, Route } from 'react-router-dom';

function App() {
  const [count, setCount] = useState(0)

  return (
    <>
<Routes>
  <Route path="/" element={<LoginSignup />} />
  <Route path="/login" element={<LoginSignup />} />
  <Route path="/homepage" element={<Homepage />} />
  <Route path="/surveypage" element={<SurveyPage />} />
  <Route path="/profilepage" element={<ProfilePage />} />
  <Route path="/admin" element={<AdminPage />} />

</Routes>

    </>
  )
}

export default App
