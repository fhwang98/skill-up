import { Outlet } from "react-router-dom";
import "@/assets/css/index.css";

function App() {
	return (
		<>
			<header></header>
			<main>
				<Outlet />
			</main>
			<footer></footer>
		</>
	);
}

export default App;
