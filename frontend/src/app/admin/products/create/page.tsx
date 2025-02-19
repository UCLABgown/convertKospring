import client from '@/lib/backend/client'
import ClientPage from './ClientPage'

export default async function Page() {

  const responseCategories = await client.GET('/products/categories')

  const responseBodyCategory = responseCategories.data!!
  return (
    <>
      <ClientPage responseBodyCategory={responseBodyCategory} />
    </>
  )
}
