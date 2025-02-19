import client from "@/lib/backend/client";
import ClientPage from './ClientPage'

export default async function Page() {
    const response = await client.GET('/products')

    const responseBody = response.data!!

    return (
        <>
            <div>
                <ClientPage responseBody={responseBody}/>
            </div>
        </>
    )
}
