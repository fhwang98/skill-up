import { useEffect, useState } from "react";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import {
	Pagination,
	PaginationContent,
	PaginationItem,
	PaginationLink,
	PaginationNext,
	PaginationPrevious,
} from "@/components/ui/pagination";
import {
	Table,
	TableBody,
	TableCell,
	TableHead,
	TableHeader,
	TableRow,
} from "@/components/ui/table";
import { getStudies } from "@/api/study";
import { useNavigate } from "react-router-dom";
import { RefreshCcw } from "lucide-react";

const PAGE_SIZE = 10;

const MainPage = () => {
	const navigate = useNavigate();

	const [studies, setStudies] = useState([]);
	const [page, setPage] = useState(0);
	const [totalPages, setTotalPages] = useState(0);
	const [keyword, setKeyword] = useState("");
	const [keywordInput, setKeywordInput] = useState("");

	useEffect(() => {
		const fetchStudies = async () => {
			try {
				const response = await getStudies({ page, keyword });
				if (!response.success) return;

				setStudies(response.data.content);
				setTotalPages(response.data.totalPages);
			} catch {
				console.log("조회 실패");
			}
		};

		fetchStudies();
	}, [page, keyword]);

	return (
		<div className="min-h-screen py-12">
			<div className="max-w-6xl mx-auto px-4">
				{/* Header */}
				<div className="mb-10 text-center">
					<h1 className="text-3xl font-bold tracking-tight text-gray-900">
						스터디 매칭
					</h1>
					<p className="mt-2 text-gray-500">함께 성장할 스터디를 찾아보세요</p>
				</div>

				{/* Search */}
				<form
					className="mb-6 flex justify-center"
					onSubmit={(e) => {
						e.preventDefault();
						setPage(0);
						setKeyword(keywordInput);
					}}
				>
					<div className="flex w-full max-w-xl gap-2">
						<Input
							placeholder="스터디 제목으로 검색"
							value={keywordInput}
							onChange={(e) => setKeywordInput(e.target.value)}
						/>
						<Button type="submit">검색</Button>
						<Button
							type="button"
							variant="outline"
							onClick={() => {
								setKeywordInput("");
								setKeyword("");
								setPage(0);
							}}
						>
							<RefreshCcw />
						</Button>
					</div>
				</form>

				{/* Create Button */}
				<div className="mb-6 flex justify-end">
					<Button
						onClick={() => navigate("/study/add")}
						className="bg-gray-500 cursor-pointer hover:bg-gray-500"
					>
						스터디 만들기
					</Button>
				</div>

				{/* Study List */}
				<div className="rounded-md border bg-white">
					<Table>
						<TableHeader>
							<TableRow>
								<TableHead className="w-sm text-center">번호</TableHead>
								<TableHead className="w-md text-center">상태</TableHead>
								<TableHead className="w-md text-center">카테고리</TableHead>
								<TableHead className="w-1/2 text-center">제목</TableHead>
								<TableHead className="w-md text-center">작성자</TableHead>
								<TableHead className="w-md text-center">작성일</TableHead>
							</TableRow>
						</TableHeader>

						<TableBody>
							{studies.length === 0 ? (
								<TableRow>
									<TableCell
										colSpan={6}
										className="py-10 text-center text-sm text-gray-500"
									>
										검색 결과가 없습니다.
									</TableCell>
								</TableRow>
							) : (
								studies.map((study, idx) => (
									<TableRow
										key={study.id}
										className="cursor-pointer hover:bg-muted/50"
										onClick={() => navigate(`/study/${study.id}`)}
									>
										<TableCell className="text-muted-foreground text-center">
											{page * PAGE_SIZE + idx + 1}
										</TableCell>

										<TableCell className="text-sm text-center">
											{study.status === "OPENED" ? (
												<Badge className="bg-green-600">모집중</Badge>
											) : (
												<Badge>모집종료</Badge>
											)}
										</TableCell>

										<TableCell>
											<Badge variant="secondary">{study.category}</Badge>
										</TableCell>

										<TableCell className="font-medium">
											{keyword && keyword !== ""
												? study.title.split(keyword).map((part, idx, arr) => (
														<span key={idx}>
															{part}
															{idx < arr.length - 1 && (
																<span className="bg-yellow-200 font-semibold">{keyword}</span>
															)}
														</span>
												  ))
												: study.title}
										</TableCell>

										<TableCell className="text-sm text-muted-foreground">
											{study.nickname}
										</TableCell>

										<TableCell className="text-sm text-muted-foreground">
											{study.createdAt?.slice(0, 10)}
										</TableCell>
									</TableRow>
								))
							)}
						</TableBody>
					</Table>
				</div>

				{/* Pagination */}

				<Pagination className="mt-12">
					<PaginationContent>
						<PaginationItem>
							<PaginationPrevious
								onClick={(e) => {
									e.preventDefault();
									page > 0 && setPage(page - 1);
								}}
							/>
						</PaginationItem>

						{Array.from({ length: totalPages }).map((_, i) => (
							<PaginationItem key={i}>
								<PaginationLink
									isActive={i === page}
									onClick={(e) => {
										e.preventDefault();
										setPage(i);
									}}
								>
									{i + 1}
								</PaginationLink>
							</PaginationItem>
						))}

						<PaginationItem>
							<PaginationNext
								onClick={(e) => {
									e.preventDefault();
									page < totalPages - 1 && setPage(page + 1);
								}}
							/>
						</PaginationItem>
					</PaginationContent>
				</Pagination>
			</div>
		</div>
	);
};

export default MainPage;
