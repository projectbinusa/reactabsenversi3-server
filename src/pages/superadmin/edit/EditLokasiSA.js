import React, { useEffect, useState } from "react";
import Navbar from "../../../components/NavbarSuper";
import Sidebar from "../../../components/SidebarUser";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faArrowLeft, faFloppyDisk } from "@fortawesome/free-solid-svg-icons";
import { useParams } from "react-router-dom/cjs/react-router-dom.min";
import Swal from "sweetalert2";
import axios from "axios";
import { API_DUMMY } from "../../../utils/api";

function EditLokasiSA() {
  const { idLokasi } = useParams();
  const [namaLokasi, setNamaLokasi] = useState("");
  const [alamat, setAlamat] = useState("");

  const getLokasi = async () => {
    try {
      const res = await axios.get(
        `${API_DUMMY}/api/lokasi/getByIdLokasi/${idLokasi}`
      );
      setNamaLokasi(res.data.namaLokasi);
      setAlamat(res.data.alamat);
    } catch (error) {
      console.log(error);
    }
  };

  const UbahLokasi = async (e) => {
    e.preventDefault();
    try {
      const lokasi = {
        namaLokasi: namaLokasi,
        alamat: alamat,
      };
      const edit = await axios.put(
        `${API_DUMMY}/api/lokasi/editByIdLokasi/${idLokasi}`,
        lokasi
      );
      setNamaLokasi(edit.data.namaLokasi);
      setAlamat(edit.data.alamat);
      Swal.fire("Berhasil", "Berhasil merubah data ", "success");
      window.location.href = "/superadmin/lokasi";
    } catch (error) {
      console.log(error);
      Swal.fire("Gagal", "Gagal merubah data ", "error");
    }
  };

  useEffect(() => {
    getLokasi();
  }, [idLokasi]);

  return (
    <div className="flex flex-col h-screen">
      <div className="sticky top-0 z-50">
        <Navbar />
      </div>
      <div className="flex h-full">
        <div className="fixed">
          <Sidebar />
        </div>
        <div className="sm:ml-64 content-page container p-8 ml-14 md:ml-64 mt-12">
          <div className="p-4">
            <div className="p-5">
              {/* <!-- Card --> */}
              <div className="w-full p-4 text-center bg-white border border-gray-200 rounded-lg shadow sm:p-8 dark:bg-gray-800 dark:border-gray-700">
                <div className="flex justify-between">
                  <h6 className="mb-2 text-xl font-bold text-gray-900 dark:text-white">
                    Edit Lokasi
                  </h6>
                </div>

                <hr />

                <div className="mt-5 text-left">
                  {/* <!-- Form Input --> */}
                  <form onSubmit={UbahLokasi}>
                    {/* <!-- Nama & Alamat Input --> */}
                    <div className="grid md:grid-cols-2 md:gap-6">
                      <div className="relative z-0 w-full mb-6 group">
                        <input
                          type="text"
                          name="nama_lokasi"
                          id="nama"
                          className="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent border-0 border-b-2 border-gray-300 appearance-none dark:text-white dark:border-gray-600 dark:focus:border-blue-500 focus:outline-none focus:ring-0 focus:border-blue-600 peer"
                          placeholder=" "
                          required
                          value={namaLokasi}
                          onChange={(e) => setNamaLokasi(e.target.value)}
                        />
                        <label
                          htmlFor="nama_lokasi"
                          className="peer-focus:font-medium absolute text-sm text-gray-500 dark:text-gray-400 duration-300 transform -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-focus:left-0 peer-focus:text-blue-600 peer-focus:dark:text-blue-500 peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6"
                        >
                          Lokasi
                        </label>
                      </div>

                      <div className="relative z-0 w-full mb-6 group">
                        <input
                          type="text"
                          name="alamat"
                          id="alamat"
                          value={alamat}
                          onChange={(e) => setAlamat(e.target.value)}
                          className="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent border-0 border-b-2 border-gray-300 appearance-none dark:text-white dark:border-gray-600 dark:focus:border-blue-500 focus:outline-none focus:ring-0 focus:border-blue-600 peer"
                          placeholder=" "
                          required
                        />
                        <label
                          htmlFor="alamat"
                          className="peer-focus:font-medium absolute text-sm text-gray-500 dark:text-gray-400 duration-300 transform -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-focus:left-0 peer-focus:text-blue-600 peer-focus:dark:text-blue-500 peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6"
                        >
                          Alamat
                        </label>
                      </div>
                    </div>
                    {/* <!-- Button --> */}
                    <div className="flex justify-between">
                      <a
                        className="focus:outline-none text-white bg-red-500 hover:bg-red-800 focus:ring-4 focus:ring-red-300 font-medium rounded-lg text-sm px-5 py-2.5 mb-2 dark:bg-red-600 dark:hover:bg-red-700 dark:focus:ring-red-900"
                        href="/superadmin/lokasi"
                      >
                        <FontAwesomeIcon icon={faArrowLeft} />
                      </a>
                      <button
                        type="submit"
                        className="text-white bg-indigo-500 hover:bg-indigo-800 focus:ring-4 focus:ring-indigo-300 font-medium rounded-lg text-sm px-5 py-2.5 mb-2 dark:bg-indigo-600 dark:hover:bg-indigo-700 focus:outline-none dark:focus:ring-indigo-800"
                      >
                        <FontAwesomeIcon icon={faFloppyDisk} />
                      </button>
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

export default EditLokasiSA;
