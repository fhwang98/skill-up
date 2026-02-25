import { useEffect, useState } from "react";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Textarea } from "@/components/ui/textarea";
import { Label } from "@/components/ui/label";
import { Badge } from "@/components/ui/badge";
import {
	Select,
	SelectContent,
	SelectItem,
	SelectTrigger,
	SelectValue,
} from "@/components/ui/select";
import { createStudy, getTags } from "@/api/study";
import { useNavigate } from "react-router-dom";
import { DatePicker } from "@/components/ui/date-picker";

const StudyCreatePage = () => {
	const navigate = useNavigate();
	const today = new Date().toISOString().split("T")[0];

	const [form, setForm] = useState({
		title: "",
		description: "",
		maxMembers: 2,
		category: null,
		tags: [],
		startDate: today,
		endDate: "",
		recruitEndDate: "",
	});

	const [tagKeyword, setTagKeyword] = useState("");
	const [tagResults, setTagResults] = useState([]);
	const [selectedIndex, setSelectedIndex] = useState(0);

	const [error, setError] = useState("");

	const isSubmitDisabled =
		!form.title ||
		!form.description ||
		!form.maxMembers ||
		!form.category ||
		!form.startDate ||
		!form.recruitEndDate;

	const handleChange = (key, value) => {
		setForm((prev) => ({ ...prev, [key]: value }));
	};

	// 로그인되지 않은 사용자
	useEffect(() => {
		const token = localStorage.getItem("accessToken");
		if (!token) {
			alert("로그인 후 작성할 수 있습니다.");
			navigate("/login", { replace: true });
		}
	}, [navigate]);

	// tag 입력창 변경 이벤트
	useEffect(() => {
		const searchTags = async () => {
			if (tagKeyword === null || tagKeyword === "") {
				setTagResults([]);
				return;
			}
			try {
				const response = await getTags(tagKeyword);
				if (!response.success) {
					setTagResults([]);
					return;
				}
				const tags = response.data;
				setTagResults(tags);
			} catch (error) {
				console.log(error);
			}
		};
		const delay = setTimeout(searchTags, 300);
		return () => clearTimeout(delay);
	}, [tagKeyword]);

	const filteredTagResults = tagResults.filter(
		(tag) => !form.tags.includes(tag)
	);

	const addTag = (tag) => {
		if (form.tags.includes(tag)) return;
		setForm((prev) => ({ ...prev, tags: [...prev.tags, tag] }));
		setTagKeyword("");
		setTagResults([]);
	};

	const removeTag = (tag) => {
		setForm((prev) => ({
			...prev,
			tags: prev.tags.filter((t) => t !== tag),
		}));
	};

	const handleKeyDown = (e) => {
		if (filteredTagResults.length === 0) return;

		switch (e.key) {
			case "ArrowDown": {
				e.preventDefault();
				setSelectedIndex((prev) =>
					Math.min(prev + 1, filteredTagResults.length - 1)
				);
				break;
			}
			case "ArrowUp": {
				e.preventDefault();
				setSelectedIndex((prev) => Math.max(prev - 1, 0));
				break;
			}
			case "Enter": {
				e.preventDefault();
				const selectedTag = filteredTagResults[selectedIndex];
				if (selectedTag) {
					addTag(selectedTag);
				}
				break;
			}
			default:
				break;
		}
	};

	const handleSubmit = async (e) => {
		e.preventDefault();
		setError(null);
		const payload = {
			title: form.title,
			description: form.description,
			maxMembers: Number(form.maxMembers),
			category: form.category,
			tags: form.tags,
			startDate: form.startDate,
			recruitEndDate: form.recruitEndDate,
			endDate: form.endDate || null,
		};

		try {
			const response = await createStudy(payload);
			if (!response.success) {
				setError(response.error.message);
				return;
			}
			alert("스터디가 생성되었습니다.");
			navigate("/");
		} catch {
			alert("스터디 생성에 실패했습니다.");
		}
	};

	return (
		<div className="min-h-screen mx-12 py-12">
			<div className="max-w-3xl mx-auto px-4">
				<h1 className="text-2xl font-bold mb-8">스터디 생성</h1>

				<form className="space-y-6" onSubmit={handleSubmit}>
					{/* 제목 */}
					<div className="space-y-2">
						<Label>스터디 제목</Label>
						<Input
							value={form.title}
							onChange={(e) => handleChange("title", e.target.value)}
						/>
					</div>

					{/* 설명 */}
					<div className="space-y-2">
						<Label>스터디 설명</Label>
						<Textarea
							className="min-h-50"
							value={form.description}
							onChange={(e) => handleChange("description", e.target.value)}
						/>
					</div>

					{/* 인원 / 카테고리 */}
					<div className="grid grid-cols-1 sm:grid-cols-2 gap-6">
						<div className="space-y-2">
							<Label>최대 인원</Label>
							<Input
								type="number"
								min={2}
								value={form.maxMembers}
								onChange={(e) => handleChange("maxMembers", e.target.value)}
							/>
						</div>

						<div className="space-y-2">
							<Label>카테고리</Label>
							<Select onValueChange={(v) => handleChange("category", v)}>
								<SelectTrigger>
									<SelectValue placeholder="카테고리 선택" />
								</SelectTrigger>
								<SelectContent>
									<SelectItem value="BACKEND">백엔드</SelectItem>
									<SelectItem value="FRONTEND">프론트엔드</SelectItem>
									<SelectItem value="ALGORITHM">알고리즘</SelectItem>
									<SelectItem value="CS">CS</SelectItem>
									<SelectItem value="PROJECT">프로젝트</SelectItem>
								</SelectContent>
							</Select>
						</div>
					</div>

					{/* 태그 */}
					<div className="space-y-2">
						<Label>태그 검색</Label>

						<div className="relative">
							<Input
								placeholder="태그를 입력하세요"
								value={tagKeyword}
								onChange={(e) => setTagKeyword(e.target.value)}
								onKeyDown={handleKeyDown}
							/>

							{filteredTagResults.map((tag, index) => (
								<div
									key={tag}
									className={`cursor-pointer px-3 py-2 text-sm border border-t-0 hover:bg-gray-100
											${index === filteredTagResults.length - 1 ? "rounded-b-md" : ""}
											${index === selectedIndex ? " bg-gray-100" : ""}`}
									onMouseEnter={() => setSelectedIndex(index)}
									onClick={() => addTag(tag)}
								>
									{tag}
								</div>
							))}
						</div>

						<div className="flex flex-wrap gap-2">
							{form.tags.map((tag) => (
								<Badge
									key={tag}
									className="cursor-pointer"
									onClick={() => removeTag(tag)}
								>
									{tag}
								</Badge>
							))}
						</div>
					</div>

					{/* 날짜 */}
					<div className="grid grid-cols-1 sm:grid-cols-2 gap-6">
						<div className="space-y-2">
							<Label>스터디 시작일</Label>
							<DatePicker
								value={form.startDate}
								onChange={(v) => handleChange("startDate", v)}
								placeholder="시작일 선택"
							/>
						</div>
						<div className="space-y-2">
							<Label>스터디 종료일 (선택)</Label>
							<DatePicker
								value={form.endDate}
								onChange={(v) => handleChange("endDate", v)}
								placeholder="종료일 선택"
								minDate={form.startDate ? new Date(form.startDate) : undefined}
							/>
						</div>
					</div>
					<div className="space-y-2">
						<Label>모집 마감일</Label>
						<DatePicker
							value={form.recruitEndDate}
							onChange={(v) => handleChange("recruitEndDate", v)}
							placeholder="모집 마감일 선택"
						/>
					</div>

					{/* 버튼 */}
					<div className="flex justify-end gap-2 pt-6">
						<Button
							type="button"
							variant="outline"
							onClick={() => navigate("/")}
							className="cursor-pointer"
						>
							취소
						</Button>
						<Button
							type="submit"
							className="cursor-pointer"
							disabled={isSubmitDisabled}
						>
							생성
						</Button>
					</div>
				</form>
			</div>
		</div>
	);
};

export default StudyCreatePage;
