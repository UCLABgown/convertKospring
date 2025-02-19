import client from '@/lib/backend/client'
import ClientPage from './ClientPage'

export default async function Page({
  searchParams,
}: {
  searchParams: {
    searchKeywordType?: 'productName' | 'category'
    searchKeyword?: string
    pageSize?: number
    page?: number
  }
}) {
  const {
    searchKeyword = '',
    searchKeywordType = 'productName',
    pageSize = 8,
    page = 1,
  } = await searchParams

  const response = await client.GET('/products', {
    params: {
      query: {
        searchKeyword,
        searchKeywordType,
        pageSize,
        page,
      },
    },
  })

  const responseBodyCategory = await client.GET('/products/categories')

  const responseBody = response.data!!

  return (
    <>
      <ClientPage
        searchKeyword={searchKeyword}
        searchKeywordType={searchKeywordType}
        page={page}
        pageSize={pageSize}
        responseBodyCategory={responseBodyCategory.data!!}
        responseBody={responseBody}
      />
    </>
  )
}
