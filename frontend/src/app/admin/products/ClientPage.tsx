'use client'

import type { components } from '@/lib/backend/apiV1/schema'
import client from '@/lib/backend/client'
import Link from 'next/link'
import { useRouter } from 'next/navigation'

export default function ClientPage({
  searchKeyword,
  searchKeywordType,
  page,
  pageSize,
  responseBodyCategory,
  responseBody,
}: {
  searchKeyword: string
  searchKeywordType: string
  page: number
  pageSize: number
  responseBodyCategory: components['schemas']['ApiResponseListProductDto']
  responseBody: components['schemas']['ApiResponsePageDtoProductDto']
}) {
  const router = useRouter()



  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault()

    const formData = new FormData(e.target as HTMLFormElement)
    const searchKeyword = formData.get('searchKeyword') as string

    router.push(
      `?page=1&searchKeywordType=productName&searchKeyword=${searchKeyword}`,
    )
  }

  const handleOnchange = async (e: React.ChangeEvent<HTMLSelectElement>) => {
    const searchKeyword = e.target.value

    router.push(
      `?page=1&searchKeywordType=category&searchKeyword=${searchKeyword}`,
    )
  }

  const handleDelete = async (productId: number) => {
    if (!confirm('정말로 삭제하시겠습니까?')) return

    const response = await client.DELETE('/products/{id}', {
      params: {
        path: {
          id: productId,
        },
      },
    })

    if (response.error) {
      alert(response.error.message)
      return
    }

    alert(response.data.message)

    router.replace('/admin/products')
  }

  return (
    <>
      <div className="flex flex-col">
        <h2 className="text-5xl font-extrabold mt-20 mb-12 text-center">
          Products
        </h2>
        <div className="flex justify-between">
          <div className="flex items-center">
            <label
              htmlFor="category"
              className="block mr-3 font-bold text-[#59473F]"
            >
              카테고리 검색
            </label>
            <select
              className="py-3 px-5 mr-8 block w-[200px] h-[50px] leading-[50px] border border-[#ddd] rounded-[8px]"
              id="category"
              onChange={handleOnchange}
            >
              <option value="전체">전체</option>
              {responseBodyCategory?.content?.map((item, index) => (
                <option key={item.category} value={item.category}>
                  {item.category}
                </option>
              ))}
            </select>
            <form className="flex items-center" onSubmit={handleSubmit}>
              <label
                htmlFor="searchKeyword"
                className="block mr-3 font-bold text-[#59473F]"
              >
                키워드 검색
              </label>
              <input
                type="text"
                name="searchKeyword"
                id="searchKeyword"
                className="py-3 px-5 mr-3 block w-[250px] h-[50px] leading-[50px] border border-[#ddd] rounded-[8px]"
              />
              <button
                type="submit"
                className="h-[50px] leading-[50px] px-5 block bg-[#59473F] text-white rounded-[8px]"
              >
                검색
              </button>
            </form>
          </div>
          <div className="flex">
            <Link
              href="/admin/products/create"
              className="h-[50px] leading-[50px] px-5 block bg-[#59473F] text-white rounded-[8px] item-end"
            >
              제품 등록
            </Link>
          </div>
        </div>

        <table className="table-fixed bg-white mt-3 shadow-[0_0_10px_0_rgba(0,0,0,0.1)]">
          <colgroup>
            <col className="w-[80px]" />
            <col className="w-[180px]" />
            <col className="" />
            <col className="w-[150px]" />
            <col className="w-[100px]" />
            <col className="w-[130px]" />
            <col className="w-[130px]" />
            <col className="w-[150px]" />
          </colgroup>
          <thead className="text-center bg-[#59473F] text-white">
            <tr>
              <th className="py-3">번호</th>
              <th>이미지</th>
              <th>제품명</th>
              <th>카테고리</th>
              <th>가격</th>
              <th>등록 날짜</th>
              <th>수정 날짜</th>
              <th>관리</th>
            </tr>
          </thead>
          <tbody>
            {responseBody.content?.items.map((item, index) => (
              <tr
                key={item.id}
                className="border-b border-[#eee] bg-white text-center"
              >
                <td className="px-3">
                  {(responseBody.content?.totalItems || 0) -
                    ((responseBody.content?.currentPageNumber || 1) - 1) *
                      (responseBody.content?.pageSize || 10) -
                    index}
                </td>
                <td>
                  <div className="w-[150px] h-[150px] p-3 inline-block">
                    <img
                      src={item.imageUrl}
                      alt={item.productName}
                      className="max-h-full mx-auto"
                    />
                  </div>
                </td>
                <td className="p-3 text-left">{item.productName}</td>
                <td className="px-3">{item.category}</td>
                <td className="px-3">{Number(item.price).toLocaleString()}</td>
                <td className="px-3">
                  {new Intl.DateTimeFormat('ko-KR', {
                    year: 'numeric',
                    month: '2-digit',
                    day: '2-digit',
                    hour: '2-digit',
                    minute: '2-digit',
                  }).format(new Date(item.createDate))}
                </td>
                <td className="px-3">
                  {new Intl.DateTimeFormat('ko-KR', {
                    year: 'numeric',
                    month: '2-digit',
                    day: '2-digit',
                    hour: '2-digit',
                    minute: '2-digit',
                  }).format(new Date(item.modifyDate))}
                </td>
                <td>
                  <Link
                    href={`/admin/products/modify/${item.id}`}
                    className="py-2 px-3 bg-[#59473F] text-white rounded-[8px]"
                  >
                    수정
                  </Link>{' '}
                  <button
                    type="button"
                    className="py-2 px-3 bg-red-800 text-white rounded-[8px]"
                    onClick={() => handleDelete(item.id)}
                  >
                    삭제
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>

        <div className="flex my-5 gap-2 justify-center">
          {Array.from(
            { length: responseBody.content?.totalPages || 0 },
            (_, i) => i + 1,
          ).map((pageNum) => (
            <Link
              key={pageNum}
              className={`px-3 py-1 border rounded shadow-[0_0_10px_0_rgba(0,0,0,0.1)] ${
                pageNum === responseBody.content?.currentPageNumber
                  ? 'bg-[#59473F] text-white'
                  : 'bg-white'
              }`}
              href={`?page=${pageNum}`}
            >
              {pageNum}
            </Link>
          ))}
        </div>
      </div>
    </>
  )
}
