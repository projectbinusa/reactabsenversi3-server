import React, { useEffect, useState } from "react";
import Navbar from "../../../components/NavbarSuper";
import Sidebar from "../../../components/SidebarUser";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faArrowLeft } from "@fortawesome/free-solid-svg-icons";
import { useParams } from "react-router-dom/cjs/react-router-dom.min";
import axios from "axios";
import { API_DUMMY } from "../../../utils/api";

function DetailOrganisasiSA() {
  const [organisasi, setOrganisasi] = useState(null);
  const { id } = useParams();

  const getOrganisasiId = async () => {
    try {
      const res = await axios.get(
        `${API_DUMMY}/api/organisasi/getById/${id}`
      );
      setOrganisasi(res.data);
    } catch (error) {
      console.log(error);
    }
  };

  useEffect(() => {
    getOrganisasiId();
  }, [id]);

  return (
    <div className="flex flex-col h-screen">
      <div className="sticky top-0 z-50">
        <Navbar />
      </div>
      <div className="flex h-full">
        <div className="fixed">
          <Sidebar />
        </div>
        <div className="sm:ml-64 content-page container p-8 ml-14 md:ml-64 mt-3">
          <div className="p-4">
            <div className="p-5 mt-10">
              {/* <!-- Card --> */}
              <div className="w-full p-4 text-center bg-white border border-gray-200 rounded-lg shadow sm:p-8 dark:bg-gray-800 dark:border-gray-700">
                <div className="flex justify-between">
                  <h6 className="mb-2 text-xl font-bold text-gray-900 dark:text-white">
                    Detail Organisasi
                  </h6>
                </div>

                <hr />

                <div className="mt-5 text-left">
                  {/* <!-- Form Input --> */}
                  <form method="post" encType="multipart/form-data">
                    <div className="mt-5 text-center">
                      {/* <!-- Mengubah kelas "text-left" menjadi "text-center" --> */}
                      <img
                        className="rounded-full w-32 h-32 mx-auto"
                        src={
                          organisasi && organisasi.fotoOrganisasi
                            ? organisasi.fotoOrganisasi
                            : "data:image/jpeg;base64,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"
                        }
                        alt="Foto Organisasi"
                      />
                    </div>

                    {/* <!-- Nama & Email Input --> */}
                    <div className="grid pt-5  md:grid-cols-2 md:gap-6">
                      <div className="relative z-0 w-full mb-6 group">
                        <input
                          type="text"
                          name="nama"
                          id="nama"
                          value={organisasi ? organisasi.namaOrganisasi : ""}
                          className="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent border-0 border-b-2 border-gray-300 appearance-none dark:text-white dark:border-gray-600 dark:focus:border-blue-500 focus:outline-none focus:ring-0 focus:border-blue-600 peer capitalize"
                          placeholder=" "
                          autoComplete="off"
                          required
                          readOnly
                        />
                        <label
                          htmlFor="nama"
                          className="peer-focus:font-medium absolute text-sm text-gray-500 dark:text-gray-400 duration-300 transform -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-focus:left-0 peer-focus:text-blue-600 peer-focus:dark:text-blue-500 peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6"
                        >
                          Nama
                        </label>
                      </div>
                      <div className="relative z-0 w-full mb-6 group">
                        <input
                          type="email"
                          name="email"
                          id="email"
                          value={organisasi ? organisasi.emailOrganisasi : ""}
                          className="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent border-0 border-b-2 border-gray-300 appearance-none dark:text-white dark:border-gray-600 dark:focus:border-blue-500 focus:outline-none focus:ring-0 focus:border-blue-600 peer"
                          placeholder=" "
                          autoComplete="off"
                          required
                          readOnly
                        />
                        <label
                          htmlFor="email"
                          className="peer-focus:font-medium absolute text-sm text-gray-500 dark:text-gray-400 duration-300 transform -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-focus:left-0 peer-focus:text-blue-600 peer-focus:dark:text-blue-500 peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6"
                        >
                          Email
                        </label>
                      </div>
                      {/* <!-- No.tlpn & Alamat Input --> */}
                      <div className="grid md:grid-cols-1 md:gap-6">
                        <div className="relative z-0 w-full mb-6 group">
                          <input
                            type="tel"
                            name="nomor_telepon"
                            id="nomor_telepon"
                            value={organisasi ? organisasi.nomerTelepon : ""}
                            className="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent border-0 border-b-2 border-gray-300 appearance-none dark:text-white dark:border-gray-600 dark:focus:border-blue-500 focus:outline-none focus:ring-0 focus:border-blue-600 peer"
                            placeholder=" "
                            autoComplete="off"
                            required
                            readOnly
                          />
                          <label
                            htmlFor="nomor_telepon"
                            className="peer-focus:font-medium absolute text-sm text-gray-500 dark:text-gray-400 duration-300 transform -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-focus:left-0 peer-focus:text-blue-600 peer-focus:dark:text-blue-500 peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6"
                          >
                            No.Telepon
                          </label>
                        </div>
                        <div className="relative z-0 w-full mb-6 group">
                          <input
                            type="text"
                            name="alamat"
                            id="alamat"
                            value={organisasi ? organisasi.alamat : ""}
                            className="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent border-0 border-b-2 border-gray-300 appearance-none dark:text-white dark:border-gray-600 dark:focus:border-blue-500 focus:outline-none focus:ring-0 focus:border-blue-600 peer capitalize"
                            placeholder=" "
                            autoComplete="off"
                            required
                            readOnly
                          />
                          <label
                            htmlFor="alamat"
                            className="peer-focus:font-medium absolute text-sm text-gray-500 dark:text-gray-400 duration-300 transform -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-focus:left-0 peer-focus:text-blue-600 peer-focus:dark:text-blue-500 peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6"
                          >
                            Alamat
                          </label>
                        </div>
                      </div>
                      {/* <!-- Kecamatan & Kabupaten Input --> */}
                      <div className="grid md:grid-cols-1 md:gap-6">
                        <div className="relative z-0 w-full mb-6 group">
                          <input
                            type="text"
                            name="kecamatan"
                            id="kecamatan"
                            value={organisasi ? organisasi.kecamatan : ""}
                            className="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent border-0 border-b-2 border-gray-300 appearance-none dark:text-white dark:border-gray-600 dark:focus:border-blue-500 focus:outline-none focus:ring-0 focus:border-blue-600 peer capitalize"
                            placeholder=" "
                            autoComplete="off"
                            required
                            readOnly
                          />
                          <label
                            htmlFor="kecamatan"
                            className="peer-focus:font-medium absolute text-sm text-gray-500 dark:text-gray-400 duration-300 transform -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-focus:left-0 peer-focus:text-blue-600 peer-focus:dark:text-blue-500 peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6"
                          >
                            Kecamatan
                          </label>
                        </div>
                        <div className="relative z-0 w-full mb-6 group">
                          <input
                            type="text"
                            name="kabupaten"
                            id="kabupaten"
                            value={organisasi ? organisasi.kabupaten : ""}
                            className="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent border-0 border-b-2 border-gray-300 appearance-none dark:text-white dark:border-gray-600 dark:focus:border-blue-500 focus:outline-none focus:ring-0 focus:border-blue-600 peer capitalize"
                            placeholder=" "
                            autoComplete="off"
                            required
                            readOnly
                          />
                          <label
                            htmlFor="kabupaten"
                            className="peer-focus:font-medium absolute text-sm text-gray-500 dark:text-gray-400 duration-300 transform -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-focus:left-0 peer-focus:text-blue-600 peer-focus:dark:text-blue-500 peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6"
                          >
                            Kabupaten
                          </label>
                        </div>
                      </div>
                      {/* <!-- Provinsi Input --> */}
                      <div className="relative z-0 w-full mb-6 group">
                        <input
                          type="text"
                          name="provinsi"
                          id="provinsi"
                          value={organisasi ? organisasi.provinsi : ""}
                          className="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent border-0 border-b-2 border-gray-300 appearance-none dark:text-white dark:border-gray-600 dark:focus:border-blue-500 focus:outline-none focus:ring-0 focus:border-blue-600 peer capitalize"
                          placeholder=" "
                          autoComplete="off"
                          required
                          readOnly
                        />
                        <label
                          htmlFor="provinsi"
                          className="peer-focus:font-medium absolute text-sm text-gray-500 dark:text-gray-400 duration-300 transform -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-focus:left-0 peer-focus:text-blue-600 peer-focus:dark:text-blue-500 peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6"
                        >
                          Provinsi
                        </label>
                      </div>
                    </div>
                    {/* <!-- Button --> */}
                    <div className="flex justify-between">
                      <a
                        className="focus:outline-none text-white bg-red-500 hover:bg-red-800 focus:ring-4 focus:ring-red-300 font-medium rounded-lg text-sm px-5 py-2.5 mr-2 mb-2 dark:bg-red-600 dark:hover:bg-red-700 dark:focus:ring-red-900"
                        href="/superadmin/organisasi"
                      >
                        <FontAwesomeIcon icon={faArrowLeft} />
                      </a>
                    </div>
                  </form>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default DetailOrganisasiSA;
